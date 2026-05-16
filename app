#!/usr/bin/env bash
set -euo pipefail

VENV_DIR=".venv"
PYTHON_BIN="python3"
PIP_BIN="$VENV_DIR/bin/pip"
PY_BIN="$VENV_DIR/bin/python"

function ensure_python() {
  if ! command -v "$PYTHON_BIN" >/dev/null 2>&1; then
    echo "ERROR: python3 not found"
    exit 1
  fi
}

function ensure_maven() {
  if ! command -v mvn >/dev/null 2>&1; then
    echo "ERROR: mvn (Maven) not found"
    exit 1
  fi
}

function create_venv() {
  if [ ! -d "$VENV_DIR" ]; then
    echo "[app] Creating virtual environment..."
    "$PYTHON_BIN" -m venv "$VENV_DIR"
  fi
}

function install_python_deps() {
  if [ ! -f "requirements.txt" ]; then
    echo "ERROR: requirements.txt not found in project root"
    exit 1
  fi

  echo "[app] Installing Python dependencies..."
  "$PIP_BIN" install --upgrade pip
  "$PIP_BIN" install -r requirements.txt
}

function mvn_package() {
  echo "[app] Building Java project..."
  mvn clean package
}

function mvn_run() {
  echo "[app] Running JavaFX application..."
  mvn clean javafx:run
}

function clean_all() {
  echo "[app] Cleaning project..."
  rm -rf target
  rm -rf "$VENV_DIR"
  rm -rf __pycache__
  find . -type d -name "__pycache__" -exec rm -rf {} + 2>/dev/null || true
  find . -type f -name "*.pyc" -delete 2>/dev/null || true
}

case "${1:-}" in
  install)
    ensure_python
    ensure_maven
    create_venv
    install_python_deps
    mvn_package
    echo "[app] Done."
    ;;
  build)
    ensure_maven
    mvn_package
    ;;
  run)
    ensure_maven
    mvn_run
    ;;
  clean)
    clean_all
    ;;
  *)
    echo "Usage:"
    echo "  ./app install   # create venv, install requirements, mvn clean package"
    echo "  ./app build     # mvn clean package"
    echo "  ./app run       # mvn clean javafx:run"
    echo "  ./app clean     # remove venv, target, python caches"
    exit 1
    ;;
esac