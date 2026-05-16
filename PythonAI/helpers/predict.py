import io
import json
import sys
from pathlib import Path

import joblib

BASE_DIR = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(BASE_DIR / "helpers"))
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding="utf-8")

from trainingAI import TechnoAssistant  # noqa: E402


def main() -> int:
    raw_data = sys.argv[1] if len(sys.argv) >= 2 else sys.stdin.read()

    if not raw_data.strip():
        print("Сбой")
        return 1

    data = json.loads(raw_data)

    model_version = data.pop("model", "v1.0.0")
    model_dir = BASE_DIR / "models" / model_version

    model_path = model_dir / f"model_RF_{model_version}.joblib"
    encoders_path = model_dir / f"encoders_RF_{model_version}.joblib"

    model_obj = joblib.load(model_path)
    features = list(model_obj.feature_names_in_)

    missing_features = [key for key in features if key not in data]
    if missing_features:
        print(f"Сбой: отсутствуют поля {missing_features}", file=sys.stderr)
        return 1

    data = {key: data[key] for key in features}

    result = TechnoAssistant.using_model(
        "single",
        data,
        model_path,
        encoders_path,
    )

    print(str(result[0]))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
