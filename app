#!/usr/bin/env bash
set -euo pipefail

PYTHON_DIR="PythonAI"
VENV_DIR="$PYTHON_DIR/.venv"
REQUIREMENTS_FILE="$PYTHON_DIR/requirements.txt"

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

function ensure_python_dir() {
  if [ ! -d "$PYTHON_DIR" ]; then
    echo "ERROR: $PYTHON_DIR directory not found"
    exit 1
  fi
}

function create_venv() {
  if [ ! -d "$VENV_DIR" ]; then
    echo "[app] Creating virtual environment in $VENV_DIR..."
    "$PYTHON_BIN" -m venv "$VENV_DIR"
  fi
}

function install_python_deps() {
  if [ ! -f "$REQUIREMENTS_FILE" ]; then
    echo "ERROR: $REQUIREMENTS_FILE not found"
    exit 1
  fi

  echo "[app] Installing Python dependencies from $REQUIREMENTS_FILE..."
  "$PIP_BIN" install --upgrade pip
  "$PIP_BIN" install -r "$REQUIREMENTS_FILE"
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

  find "$PYTHON_DIR" -type d -name "__pycache__" -exec rm -rf {} + 2>/dev/null || true
  find "$PYTHON_DIR" -type f -name "*.pyc" -delete 2>/dev/null || true
}

case "${1:-}" in
  install)
    ensure_python
    ensure_maven
    ensure_python_dir
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
    echo "  ./app install   # create PythonAI/.venv, install PythonAI/requirements.txt, mvn clean package"
    echo "  ./app build     # mvn clean package"
    echo "  ./app run       # mvn clean javafx:run"
    echo "  ./app clean     # remove PythonAI/.venv, target, python caches"
    exit 1
    ;;
esac