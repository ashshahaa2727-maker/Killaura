import json
import os
import numpy as np
import torch
import torch.nn as nn

WINDOW = 40
STRIDE = 10
SEQ = 64
EPS = 1e-6
RHO_LIMIT = 0.95
PATIENCE = 100
AUTO_CAP = 1200
BATCH = 256
LR = 1e-2
MIX = 3

def wrap(a):
    return (a + 180.0) % 360.0 - 180.0

def quantize(value, gcd):
    return np.round(value / gcd) * gcd if gcd > 0 else value

def squash(u, limit):
    return float(np.clip(np.sinh(np.clip(u, -8.0, 8.0)), -limit, limit))

def unsquash(value):
    return float(np.arcsinh(value))

class Policy(nn.Module):
    def __init__(self, features, hidden, mix=MIX):
        super().__init__()
        self.hidden = hidden
        self.mix = mix
        self.gru = nn.GRU(features, hidden, batch_first=True)
        self.head = nn.Linear(hidden, 1 + 6 * mix)

    def forward(self, x, state=None):
        out, state = self.gru(x, state)
        head = self.head(out)
        parts = head[..., 1:].unflatten(-1, (self.mix, 6))
        return (parts[..., 1:3], parts[..., 3:5].clamp(-4.0, 1.5), head[..., 0:1],
                torch.tanh(parts[..., 5]) * RHO_LIMIT,
                torch.log_softmax(parts[..., 0], dim=-1), state)

def loss_of(mu, log_sigma, extra, rho, logw, y):
    z = (y[..., 0:4:2].unsqueeze(-2) - mu) / log_sigma.exp()
    d = (1.0 - rho ** 2).clamp(min=1e-4)
    per = ((z[..., 0] ** 2 - 2 * rho * z[..., 0] * z[..., 1] + z[..., 1] ** 2) / (2 * d)
           + log_sigma.sum(-1) + 0.5 * torch.log(d))
    alive = y[..., 1]
    live = (-torch.logsumexp(logw - per, dim=-1) * alive).sum() / alive.sum().clamp(min=1.0)
    return live
