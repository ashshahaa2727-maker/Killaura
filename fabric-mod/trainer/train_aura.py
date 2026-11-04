import argparse
import csv
import glob
import math
import os
import sys

import numpy as np

from common import (MIX, compare, detector_auc, export, quantize, runs_of, squash, step,
                    stream_stats, train_policy, unsquash, verify, windows, wrap)

FEATURES = 17
LIMITS = (179.0, 90.0)
MIN_SIZE = 0.5
MIN_LEN = 24
FREEZE_CUT = 40
MAX_RUN = float(FREEZE_CUT)
ERROR_STEPS = 65
DEFAULTS = (0.70, 4, 0.45)
KNOB = (DEFAULTS, (0.70, 1, 0.45), (0.70, 8, 0.45), (0.70, 4, 1.00))

NUMERIC = ("gcd", "yaw", "pitch", "dyaw", "dpitch", "rx", "ry", "rz", "bw", "bh", "dist", "on", "atk")
COLUMNS = ("t", "clean", "has", "tid") + NUMERIC


def features(ey, ep, dey, dep, vy, vp, dy1, dp1, dy2, dp2,
             dist, size_yaw, size_pitch, on, since, run):
    a = lambda v: float(np.arcsinh(v)) / 3.0
    return [
        a(ey), a(ep), a(dey), a(dep), a(vy), a(vp),
        a(dy1), a(dp1), a(dy2), a(dp2),
        a(ey / size_yaw), a(ep / size_pitch),
        math.log(max(dist, 0.05) + 0.5) / 2.0,
        math.log(size_yaw) / 3.0,
        1.0 if on > 0.5 else 0.0,
        since / 20.0,
        run / MAX_RUN,
    ]
