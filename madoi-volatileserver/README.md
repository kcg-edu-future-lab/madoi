# madoi-volatileserver

コラボレーションツール用メッセージングサーバ "Madoi" (円居 or 惑い)のサーバ実装です。
Springbootを使用しており、起動すると、websocket接続を待ち受けます。

## 概要

madoi-volatileserverは、Madoiの、情報の永続化を考慮していないサーバ実装です。
全てオンメモリで動作し、何ら情報を保存しません。
ルームやピアのプロファイル情報やメッセージ履歴もメモリ上にのみ保持するため、サーバを停止すると全て消えます。


## Apache2.0でのリバースプロキシ

Apacheをリバースプロキシを設ける場合の設定例を以下に示します。
まず、proxy_httpモジュールとproxy_wstunnelモジュールを導入する必要があります。

```
a2enmod proxy_http
a2enmod proxy_wstunnel
```

サイト毎の設定に、以下を追加してください。

```
# WebSocket接続を30分間タイムアウトしない
ProxyTimeout 1800

# コンテキストパスが"madoi"の場合の設定例
<Location /madoi>
  ProxyPass http://localhost:8080/madoi
  ProxyPassReverse http://localhost:8080/madoi
</Location>
<Location /app/rooms>
  ProxyPass ws://localhost:8080/madoi/rooms
</Location>
```

