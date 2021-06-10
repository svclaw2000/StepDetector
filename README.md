# StepDetector
> KyuHwon Park and Young-Seob Jeong, "Step Detection When Users Vertically Hold Smartphones," in Proceedings of the 15th Asia Pacific International Conference on Information Science and Technology (APIC-IST), Seoul, South Korea, pp. 1-3, July 5-7, 2020.

## 📄 Introduction
"Step Detection When Users Vertically Hold Smartphones" 논문 관련 코드
실내 네비게이션, 실내 대화 시스템 관련 서비스의 사용자 만족도를 높이기 위해 사용자의 정확한 실내 위치 정보 필요
사용자가 이동 중일 때만 위치를 업데이트할 수 있도록 사용자의 걸음 여부 감지

## 🔑 Features
- 스마트폰의 가속도 센서와 자이로스코프 센서를 이용하여 수집한 데이터로 분류 진행
- 클라이언트가 1초마다 수집한 데이터를 서버로 보내고, 서버에서 분류하여 결과 반환
- 학습 데이터
  | Action | Detail | Time | Rows |
  |--------|--------|------|------|
  | Walk | Forward | 329s | 5554 |
  | Walk | Looking left | 359s | 6001 |
  | Walk | Looking right | 359s | 6057 |
  | Stop | Stop | 269s | 4505 |
  | Stop | Turn left | 278s | 4771 |
  | Stop | Turn right | 273s | 4674 |
  | Stop | Tilt up | 283s | 4758 |
  | Stop | Tilt down | 268s | 4525 |
  | Stop | Typing words | 187s | 3217 |

## 📱 Test Environments
```
[BackEnd] Ubuntu 16.04 LTS
[BackEnd] Python 3.7
[FrontEnd] Android 7.0
```

## 💻 Framework and Dependency
```
Django 1.1.2
scikit-learn 0.21.2
```

## 👨‍💻 Members
- [박규훤](https://github.com/svclaw2000): [FrontEnd/BackEnd/ML&DL Engineering] svclaw2000@gmail.com
