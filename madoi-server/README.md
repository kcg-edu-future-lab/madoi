# madoi-server

"Madoi" (円居 or 惑い)のサーバです。
Springbootを使用して開発しています。


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

