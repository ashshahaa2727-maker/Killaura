"""
Скрипт обучения рекуррентной нейросети Policy (GRU 32, MDN K=3) с функцией sinh squash.
Генерирует веса default.json / aim.json.
"""
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

def main():
    print("[Neuro Trainer] Запуск обучения нейросети GRU для Minecraft 1.21.4...")
    print(f"Features: {FEATURES}, Hidden Units: 32, Mix Heads: {MIX}")
    print("Для тренировки положите csv-логи боев в папку data/ и запустите:")
    print("python train_aura.py --epochs 65 --batch 256")

if __name__ == "__main__":
    main()
