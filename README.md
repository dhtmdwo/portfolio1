<div align="center">
  <img src="./readme-images/icon.png" alt="logo" width="550">
</div>

---

<br>

<div align="center">

🏃🏻‍♀️ [WHTHIS 데모 사이트 바로가기](https://www.wmthis.n-e.kr) 💨

</div>
<br>


---

## 📌 목차

- [👥 팀원 구성](#-팀원-구성)
- [🔧 기술 스택](#-기술-스택)
- [🔮 프로젝트 소개](#-프로젝트-소개)
    - [자동 재고 관리 시스템](#자동-재고-관리-시스템)
    - [POS 기능으로 재고 자동화 관리](#pos-기능으로-재고-자동화-관리)
    - [매장 관리 기능](#매장-관리-기능)
    - [재료 공유 및 거래 시스템](#재료-공유-및-거래-시스템)
- [⚠️ 서비스 이용 전 확인](#서비스-이용-전-반드시-확인해-주세요)
- [🗂️ 프로젝트 기획](#-프로젝트-기획)
- [📜 프로젝트 설계](#-프로젝트-설계)
- [📚 기술 소개](#-기술-소개)



---

# <img src="https://i.namu.wiki/i/i4lq7xgSiZoHNJFK7Zcon9-4AJtQMNsCoezqrOcBIgoIst9dfGMn863K0Qmru8prJDtUvOLHky_uuVwGqkuE6MhHGBwrLeMP7cc72-XSaHUdmz8r11xhxCo-pKesnUfavCgH9etSvzhKezlFQ37MIg.svg" alt="logo" width="30"> 팀원 구성

<div align="center">
  <table>
    <tr>
      <th>서찬영</th>
      <th>천태훈</th>
      <th>오승재</th>
      <th>김유진</th>
    </tr>
    <tr>
      <td><img src="./readme-images/no.webp" width="100" height="100"/></td>
       <td><img src="./readme-images/honoka.webp" width="100" height="100"/></td>
       <td><img src="./readme-images/ni.webp" width="100" height="100"/></td>
       <td><img src="./readme-images/eri.webp" width="100" height="100"/></td>
    </tr>
    <tr>
      <td><a href="https://github.com/chan0o0seo">@chan0o0seo</a></td>
      <td><a href="https://github.com/taehoon0518">@taehoon0518</a></td>
      <td><a href="https://github.com/dhtmdwo">@dhtmdwo</a></td>
      <td><a href="https://github.com/kuj7882">@kuj7882</a></td>
    </tr>
  </table>
</div>

<br>

---

# 🔧 기술 스택

### Backend


<img src="https://img.shields.io/badge/SpringBatch-%236DB33F?style=&logo=spring">&nbsp;
<img src="https://img.shields.io/badge/Java-%23ED8B00?logo=openjdk&logoColor=white">&nbsp;
<img src="https://img.shields.io/badge/SpringBoot-%236DB33F?logo=springboot&logoColor=white">&nbsp;
<img src="https://img.shields.io/badge/SpringSecurity-%236DB33F?logo=springsecurity&logoColor=white">&nbsp;
<img src="https://img.shields.io/badge/Gradle-%2302306C?logo=gradle&logoColor=white">&nbsp;
<img src="https://img.shields.io/badge/JWT-%23000000?logo=jsonwebtokens&logoColor=white">&nbsp;


### DB


<img src="https://img.shields.io/badge/Redis-%23DC382D?logo=redis&logoColor=white">&nbsp;
<img src="https://img.shields.io/badge/MariaDB-%23003545?logo=mariadb&logoColor=white">&nbsp;

### Message Queue

<img src="https://img.shields.io/badge/Kafka-%2300111C?logo=apachekafka&logoColor=white">&nbsp;
<img src="https://img.shields.io/badge/ZooKeeper-%236C3A1D?logo=apache&logoColor=white">&nbsp;

### DevOps


<img src="https://img.shields.io/badge/Ansible-%23000000?logo=ansible&logoColor=white">&nbsp;
<img src="https://img.shields.io/badge/Helm-%230074C1?logo=helm&logoColor=white">&nbsp;

### CI/CD

<img src="https://img.shields.io/badge/Kubernetes-%23326CE5?logo=kubernetes&logoColor=white">&nbsp;
<img src="https://img.shields.io/badge/Docker-002260?style=flat&logo=docker&logoColor=white">&nbsp;
<img src="https://img.shields.io/badge/Jenkins-CF4045?style=flat&logo=jenkins&logoColor=white">&nbsp;
<img src="https://img.shields.io/badge/GitHub%20Webhook-%23181717?logo=github&logoColor=white">&nbsp;

### Monotoring

<img src="https://img.shields.io/badge/Grafana-F46800?style=flat&logo=Grafana&logoColor=white">&nbsp;
<img src="https://img.shields.io/badge/Prometheus-E6522C?style=flat&logo=Prometheus&logoColor=white">&nbsp;

### Test&VCS

<img src="https://img.shields.io/badge/Locust-%23000000?logo=python&logoColor=white">&nbsp;
<img src="https://img.shields.io/badge/Git-F05032?style=flat&logo=git&logoColor=white">&nbsp;

### Cooperation

<img src="https://img.shields.io/badge/GitHub-181717?style=flat&logo=github&logoColor=white">&nbsp;
<img src="https://img.shields.io/badge/Discord-5865F2?style=flat&logo=Discord&logoColor=white">&nbsp;
<img src="https://img.shields.io/badge/Figma-F24E1E?style=flat&logo=Figma&logoColor=white">&nbsp;

<br>

---

# 🔮 프로젝트 소개

## **WMTHIS: 효율적인 재고 및 영업 관리 솔루션**

**WMTHIS** 솔루션은  
📊 **매출 데이터 기반의 영업 관리**와  
📦 **체계적인 재고 관리**를 제공합니다.

이를 통해 **남는 재료를 인근 매장과 쉽게 공유**하여  
✅ **폐기율을 낮추고**  
✅ **원가 절감 효과**를 기대할 수 있습니다.

---

## **프로젝트 목표**

```재고 관리의 부담```: 매출이 발생할 때마다 관련된 재고가 자동으로 차감되도록 설계되어,
더 이상 수기로 일일이 기록할 필요가 없습니다.

``` 매장 간 재고 거래를 활성화```: 유통기한이 임박한 재료나
갑작스럽게 필요한 재료가 있을 때,
주변 매장들과 실시간으로 공유하고 거래할 수 있는 기능을 제공합니다.

```매장 인사이트 제공```: 매출, 재고, 판매 메뉴 데이터를 분석하여
운영 개선에 필요한 인사이트를 제공합니다.


---

## **세부기능**

1. ```주문시 재고 자동 차감``` <br>
   고객이 메뉴를 주문하면 해당 메뉴에 등록된 레시피를 기반으로 필요한 재고 소요량을 자동 계산하고, 실재고에서 해당 수량을 차감합니다.

1. ```매장간 거래``` <br> 남는 재고를 다른 매장과 거래할 수 있도록 장터 형태의 기능을 제공하며, 매장 간 자원 재활용을 도와줍니다.

1. ```매장 현황 분석``` <br> 기간별로 매출, 주문 방식, 판매 경로, 재고 소모 유형 등을 분석하여 매장 운영의 효율성을 높입니다.

1. ```구매요청알림``` <br> 다른 매장에서 내 판매글에 대해 구매 요청이 들어오면 실시간으로 알림을 제공하여 거래 흐름을 놓치지 않도록 도와줍니다.


---

# 🗂️ 프로젝트 기획

<br>

▶ [**🕙 WBS 바로가기**](https://docs.google.com/spreadsheets/d/1LdWS6icJXQ0v-Flu6HQHA01YwZ88RcX8zlemLsZBi1U/edit?gid=0#gid=0)

▶ [**📑 요구사항 정의서 바로가기**](https://docs.google.com/spreadsheets/d/16wSc1cXDfdekU2iw4pQBwFMDCW2UYdl9KT6ui1QOgOY/edit?gid=0#gid=0)

<br>

---

# 📜 프로젝트 설계

## **📈 시스템 아키텍처**
![image](https://github.com/user-attachments/assets/d4070810-f5ea-48a7-a58e-ba47aa74d8de)


[**🎨 ERD**](https://www.erdcloud.com/d/gaWyQgoD7gXc2LNvG)

[**📃 API 명세서**](http://www.cheeeze.kro.kr/swagger-ui/index.html)


<br>

---

# 📚 기술 소개

> 자세한 내용은 Wiki에서 확인할 수 있습니다.

### ▶ [**Wiki 바로가기**](https://github.com/beyond-sw-camp/be12-fin-5verdose-WMTHIS-BE/wiki)

<br>
