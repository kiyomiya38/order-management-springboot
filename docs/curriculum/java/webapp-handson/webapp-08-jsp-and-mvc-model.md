# WebApp-08 ハンズオン: JSPとMVCモデル

対応参考資料: `J5-08_JSPとMVCモデル.pdf`

## 1. この資料のゴール
- JSPとServletの役割分担を説明できる
- MVC（Model / View / Controller）構成を説明できる
- ControllerからJSPへ `forward` でデータ受け渡しできる

---

## 2. 事前準備
- WebApp-07 まで完了
- WebApp-04 のDAOコードを再利用できる状態

---

## 3. 先に覚えるポイント
1. JSPはHTML中心で画面を作りやすい（View担当）
2. Controller（Servlet）は流れ制御、Modelは業務処理/DB処理を担当
3. `request.setAttribute` + `forward` で画面へ値を渡す
4. JSPは `WEB-INF` 配下に置くと直接URLアクセスを防げる

---

## 4. ハンズオン

目的:
- MVC分離で「検索 -> 一覧表示」の最小機能を実装する

完了条件:
- Controller経由でJSPを表示し、一覧データが描画される

作業プロジェクト: `webapp_handson08`

### Step 0: 構成を作成
推奨配置:
- `src/controller` : Servlet
- `src/model` : Service/DAO/DTO
- `WebContent/WEB-INF/view` : JSP

### Step 1: Model（DTO + Service）作成
`src/model/SurveyDto.java`

```java
package model;

public class SurveyDto {
    private final String userName;
    private final int satisfactionLevel;
    private final String commentText;

    public SurveyDto(String userName, int satisfactionLevel, String commentText) {
        this.userName = userName;
        this.satisfactionLevel = satisfactionLevel;
        this.commentText = commentText;
    }

    public String getUserName() {
        return userName;
    }

    public int getSatisfactionLevel() {
        return satisfactionLevel;
    }

    public String getCommentText() {
        return commentText;
    }
}
```

`src/model/SurveySearchService.java`

```java
package model;

import java.util.ArrayList;
import java.util.List;

public class SurveySearchService {
    // 学習用の簡易実装（本番ではDAO経由でDB取得）
    public List<SurveyDto> findByMinLevel(int minLevel) {
        List<SurveyDto> list = new ArrayList<>();
        list.add(new SurveyDto("Tanaka", 5, "使いやすい"));
        list.add(new SurveyDto("Suzuki", 3, "普通"));
        list.add(new SurveyDto("Sato", 4, "分かりやすい"));

        List<SurveyDto> filtered = new ArrayList<>();
        for (SurveyDto dto : list) {
            if (dto.getSatisfactionLevel() >= minLevel) {
                filtered.add(dto);
            }
        }
        return filtered;
    }
}
```

### Step 2: Controller作成
`src/controller/SurveySearchController.java`

```java
package controller;

import java.io.IOException;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.SurveyDto;
import model.SurveySearchService;

public class SurveySearchController extends HttpServlet {
    private final SurveySearchService service = new SurveySearchService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String minLevelRaw = request.getParameter("minLevel");
        int minLevel = (minLevelRaw == null || minLevelRaw.isEmpty()) ? 1 : Integer.parseInt(minLevelRaw);

        List<SurveyDto> surveyList = service.findByMinLevel(minLevel);
        request.setAttribute("surveyList", surveyList);
        request.setAttribute("minLevel", minLevel);

        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/view/survey-list.jsp");
        rd.forward(request, response);
    }
}
```

### Step 3: JSP作成（View）
`WebContent/WEB-INF/view/survey-list.jsp`

```jsp
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="model.SurveyDto" %>
<%
  List<SurveyDto> surveyList = (List<SurveyDto>) request.getAttribute("surveyList");
  Integer minLevel = (Integer) request.getAttribute("minLevel");
%>
<!DOCTYPE html>
<html lang="ja">
<head>
  <meta charset="UTF-8">
  <title>アンケート一覧</title>
</head>
<body>
  <h1>アンケート一覧（満足度 <%= minLevel %> 以上）</h1>
  <table border="1">
    <tr>
      <th>名前</th>
      <th>満足度</th>
      <th>コメント</th>
    </tr>
    <% for (SurveyDto dto : surveyList) { %>
    <tr>
      <td><%= dto.getUserName() %></td>
      <td><%= dto.getSatisfactionLevel() %></td>
      <td><%= dto.getCommentText() %></td>
    </tr>
    <% } %>
  </table>
</body>
</html>
```

### Step 4: `web.xml` 設定と確認（仕上げ）
`WebContent/WEB-INF/web.xml`

```xml
<servlet>
  <servlet-name>SurveySearchController</servlet-name>
  <servlet-class>controller.SurveySearchController</servlet-class>
</servlet>
<servlet-mapping>
  <servlet-name>SurveySearchController</servlet-name>
  <url-pattern>/survey-search</url-pattern>
</servlet-mapping>
```

アクセス:

```text
http://localhost:8080/webapp_handson08/survey-search?minLevel=4
```

---

## 5. ミニ演習（10分）
1. `SurveySearchService` をDAO利用版に置き換える。
2. 検索フォームJSPを追加し、`minLevel` を選べるようにする。
3. `survey-list.jsp` の表示文字列に `HtmlEscaper` を適用する。

---

## 6. つまずきポイント
- JSPを直接URLで開いてしまう
  -> `WEB-INF/view` に置き、Controller経由で表示する
- ControllerにSQLやHTMLを混在させる
  -> 役割をModel/View/Controllerに分割する
- `request` 属性名の不一致で表示できない
  -> `setAttribute` とJSP側取得名を一致させる

