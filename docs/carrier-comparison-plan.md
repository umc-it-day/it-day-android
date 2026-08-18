# 통신사 변경 혜택 비교 및 추천 기능 개발 계획서 (Carrier Comparison Plan)

## 📌 1. 개요
- **목적**: 사용자가 현재 이용 중인 통신사 멤버십 외에, 다른 통신사로 변경했을 때 받을 수 있는 혜택(제휴 할인, 등급별 혜택 등)을 비교 분석하여 추천해 주는 기능.
- **진입점**: 홈 화면 내 `"통신사 바꾸면 더 받을 수도 있어요"` 배너 (현재는 비활성화 처리됨).
- **관련 화면**: `com.umc.itday.ui.home.CarrierComparisonScreen`

---

## 📡 2. 활용할 서버 API 명세

| 기능 영역 | HTTP Method & Endpoint | 반환 데이터 (DTO) | 설명 |
| :--- | :--- | :--- | :--- |
| **현재 내 멤버십 조회** | `GET /api/members/me/membership` | `MembershipInfoDto`<br/>(`telecomLabel`, `telecomGrade`) | 상단 '내 멤버십' 영역에 현재 유저의 통신사와 등급 바인딩 |
| **전체 통신사 목록** | `GET /api/telecoms` | `List<TelecomDto>`<br/>(`telecom`, `label`) | 비교 대상 통신사 목록 (`SKT`, `KT`, `LGU+`) 조회 |
| **통신사별 전체 등급/혜택** | `GET /api/telecoms/{telecom}/grades` | `List<TelecomGradeDto>`<br/>(`membershipId`, `telecomGrade`, `gradeContent`) | 타 통신사의 모든 등급 및 혜택 정보 조회 |
| **전체 제휴 브랜드 목록** | `GET /api/brands` | `List<BrandDto>`<br/>(`brandId`, `brandName`, `brandImg`, `category`) | 각 통신사별 대표 제휴 혜택 미니 카드 매핑 |
| **통신사 멤버십 변경/적용** | `PATCH /api/members/me/membership` | `UpdateMembershipRequestDto`<br/>(`membershipId`) | 추천 카드 클릭 후 '이 통신사로 변경' 시 즉시 멤버십 업데이트 |

---

## 🔄 3. 데이터 조합 및 추천 로직

1. **내 현재 통신사 식별**:
   - `GET /api/members/me/membership`을 호출하여 현재 사용자의 통신사(예: `SKT`)를 확인합니다.
2. **타 통신사 필터링 및 데이터 수집**:
   - `GET /api/telecoms` 결과 중 현재 사용자의 통신사를 제외한 나머지 통신사(예: `KT`, `LGU+`)를 추출합니다.
   - 각 타 통신사에 대해 `GET /api/telecoms/{telecom}/grades`를 호출하여 최상위 등급(VVIP, VIP, Diamond 등)의 혜택 데이터를 가져옵니다.
3. **추천 카드 구성**:
   - 가장 제휴 혜택 폭이 큰 대표 멤버십 카드에 `"추천 🔥"` 뱃지를 부여합니다.
   - `BrandBenefitHelper` 및 `GET /api/brands` 정보를 바탕으로 각 등급의 대표 혜택(CU 15%, 스타벅스 사이즈업, CGV 4,000원 할인 등)을 4개 미니 카드로 노출합니다.
4. **멤버십 변경 연동**:
   - 카드 상세 또는 '변경하기' 클릭 시 `PATCH /api/members/me/membership`을 호출하여 사용자의 멤버십 정보를 갱신합니다.

---

## 🏗️ 4. 구현 단계 및 체크리스트

- [ ] **Repository 계층 확장**:
  - `CarrierComparisonRepository` 또는 `HomeRepository`에 `getCarrierRecommendations()` 함수 구현
- [ ] **ViewModel 상태 정의**:
  - `CarrierComparisonUiState` (현재 멤버십, 추천 목록, 로딩/에러 상태)
- [ ] **UI 바인딩**:
  - `CarrierComparisonScreen.kt`에서 하드코딩 더미 데이터 제거 및 실제 `StateFlow` 데이터 바인딩
- [ ] **홈 화면 배너 재활성화**:
  - 기능 개발 완료 후 `HomeScreen.kt`의 `CarrierComparisonBanner` 주석 해제 및 배포
