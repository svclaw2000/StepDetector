# StepDetector
> KyuHwon Park and Young-Seob Jeong, "Step Detection When Users Vertically Hold Smartphones," in Proceedings of the 15th Asia Pacific International Conference on Information Science and Technology (APIC-IST), Seoul, South Korea, pp. 1-3, July 5-7, 2020.

## π Introduction
"Step Detection When Users Vertically Hold Smartphones" λΌλ¬Έ κ΄λ ¨ μ½λ
μ€λ΄ λ€λΉκ²μ΄μ, μ€λ΄ λν μμ€ν κ΄λ ¨ μλΉμ€μ μ¬μ©μ λ§μ‘±λλ₯Ό λμ΄κΈ° μν΄ μ¬μ©μμ μ νν μ€λ΄ μμΉ μ λ³΄ νμ
μ¬μ©μκ° μ΄λ μ€μΌ λλ§ μμΉλ₯Ό μλ°μ΄νΈν  μ μλλ‘ μ¬μ©μμ κ±Έμ μ¬λΆ κ°μ§

## π Features
- μ€λ§νΈν°μ κ°μλ μΌμμ μμ΄λ‘μ€μ½ν μΌμλ₯Ό μ΄μ©νμ¬ μμ§ν λ°μ΄ν°λ‘ λΆλ₯ μ§ν
- ν΄λΌμ΄μΈνΈκ° 1μ΄λ§λ€ μμ§ν λ°μ΄ν°λ₯Ό μλ²λ‘ λ³΄λ΄κ³ , μλ²μμ λΆλ₯νμ¬ κ²°κ³Ό λ°ν
- νμ΅ λ°μ΄ν°
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

## π± Test Environments
```
[BackEnd] Ubuntu 16.04 LTS
[BackEnd] Python 3.7
[FrontEnd] Android 7.0
```

## π» Framework and Dependency
```
Django 1.1.2
scikit-learn 0.21.2
```

## π¨βπ» Members
- [λ°κ·ν€](https://github.com/svclaw2000): [FrontEnd/BackEnd/ML&DL Engineering] svclaw2000@gmail.com
