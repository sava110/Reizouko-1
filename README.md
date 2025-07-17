# Spring Boot Webアプリケーション開発環境構築（Eclipse + Maven + Thymeleaf）

このプロジェクトでは、**Eclipse IDE** を用いて **Spring Boot + Maven + Thymeleaf** によるWebアプリケーションを開発するための環境構築手順をまとめています。

---

## ✅ 前提条件

- Java JDK 17 以上がインストールされていること
- Eclipse IDE がインストールされていること（[Eclipse公式サイト](https://www.eclipse.org/downloads/)）
- インターネット接続（Maven依存関係の解決に必要）

---

## 🛠️ 開発環境のセットアップ手順

### 1. Javaのインストール確認

```bash
java -version
```

### 2. EclipseにSpringツールをインストール
1. Eclipseを起動
2. メニューから Help → Eclipse Marketplace... を開く
3. 「Spring Tools」で検索
4. Spring Tools (Spring IDE and Spring Tool Suite) をインストール
5. Eclipseを再起動

### 3. Spring Bootプロジェクトの作成
1. `File`→`New`→`Spring Starter Project`
2. 以下を設定：
   - Name: `my-spring-app`
   - Type: Maven
   - Packaging: Jar
   - Java: 17
   - Dependencies:
     - Spring Web
     - Thymeleaf
3. 完了後、プロジェクトが生成されます

### 4. プロジェクト構成
```bash
my-spring-app/
├── src/main/java
│   └── com/example/demo
│       └── DemoApplication.java
├── src/main/resources
│   ├── templates/               # Thymeleafテンプレートを格納
│   └── application.properties
├── pom.xml                      # Mavenの設定ファイル
```

### 5.application.properties の設定例
```bash
spring.thymeleaf.prefix=classpath:/templates/
spring.thymeleaf.suffix=.html
spring.thymeleaf.cache=false
server.port=8080
```

### 6. 起動と確認
1. `DemoApplication.java`を右クリック→`Run As`→`Spring Boot App`
2. ブラウザで`http://localhost:8080`にアクセスし、起動確認
