# madoi-core

次バージョンでの整理

分散共有オブジェクトフレームワーク 'madoi' のコア部分。メッセージ配信処理のみを実装。

## 用語
|用語|説明|
|---|---|
|メッセージ(Message)|クライアントやサーバ間でやりとりされるデータ。JSON形式|
|ピア(Peer)|クライアントを識別する単位。IDとプロファイル(name=valueのセット)を持ち、ルームに参加してメッセージ通信を行う|
|サーバ(Server)|ピアが接続する対象。複数のピアとルームを管理し、ルーム内でのメッセージ配信を実現する|
|ルーム(Room)|ピアが参加する、メッセージ配信の区切りとなるグループ。|

## 基本メッセージ定義

```mermaid
classDiagram
  namespace データクラス {
    class ShareConfig{
      type: "beforeExec" | "afterExec"
      maxLog: number
    }
    class PeerProfile{
      id: string
      order: number  // サーバ側で振られる
      [key: string]: any
    }
    class RoomSpec{
      id: string
      [key: string]: any
    }
    class FunctionDefinition{
      id: number
      name: string
      shareConfig: ShareConfig
    }
    class MethodDefinition{
      id: number
      name: string
      shareConfig: ShareConfig
    }
    class ObjectDefinition{
      id: number
      name: string
      methods: MethodDefinition[]
    }
  }
  ObjectDefinition*--MethodDefinition
  MethodDefinition*--ShareConfig
  FunctionDefinition*--ShareConfig
```
```mermaid
classDiagram
  namespace 基本メッセージ {
    class Message{
      +type: string
      +sender: string
      +castType: string
      +recipients: string[]
    }
    class PeerToServerMessage{
      +castType: "PEERTOSERVER"
      +recipients: string[0]
    }
    class ServerToPeerMessage{
      +sender: "__SERVER__"
      +castType: "SERVERTOPEER"
      +recipients: string[]
    }
    class PeerToPeerMessage{
      +castType: "UNICAST" | "MULTICAST" |
       "BROADCAST" | "OTHERCAST" | "SELFCAST"
      +recipients: string[]
    }
  }
  Message<|--PeerToServerMessage
  Message<|--ServerToPeerMessage
  Message<|--PeerToPeerMessage
  namespace ルーム管理{
    class EnterRoomAllowed {
      self: PeerProfile
      peers: PeerProfile[]
      room: RoomProfile
      history: History[]
    }
    class EnterRoomDenied {
      selfId: string
      roomId: string
    }
    class PeerEntered {
      peer: PeerProfile
    }
    class PeerLeaved{
      peerId: string
    }
    class EnterRoom {
      self: PeerProfile
      room: RoomProfile
    }
    class UpdateRoomProfile{
      roomId: string
      updates: object
      deletes: string[]
    }
    class UpdatePeerProfile{
      peerId: string
      updates: object
      deletes: string[]
    }
  }
  PeerToServerMessage<|--EnterRoom 
  ServerToPeerMessage<|--EnterRoomAllowed
  ServerToPeerMessage<|--EnterRoomDenied
  PeerToPeerMessage<|--UpdateRoomProfile 
  ServerToPeerMessage<|--PeerEntered
  ServerToPeerMessage<|--PeerLeaved
  namespace オブジェクト管理{
    class DefineFunction{
      spec: FunctionDefinition
    }
    class DefineObject{
      spec: ObjectDefinition
    }
    class InvokeMethod{
      objId: number
      methodId: number
      args: any[]
    }
    class InvokeFunction{
      functionId: number
      args: any[]
    }
    class UpdateObjectState{
    }
  }
  PeerToServerMessage<|--DefineFunction
  PeerToPeerMessage<|--InvokeFunction
  PeerToServerMessage<|--DefineObject
  PeerToPeerMessage<|--UpdatePeerProfile 
  PeerToPeerMessage<|--InvokeMethod
  PeerToPeerMessage<|--UpdateObjectState
```
```mermaid
classDiagram
  note "History = (PeerEntered | PeerLeaved | PeerToPeerMessage)[]"
```
##　同期機能
* オブジェクトリビジョン管理？
  * オブジェクトのリビジョン(メソッド呼び出しのたびに+1)を管理する。
  * 何が嬉しい？リビジョンの不整合が検出できる。
  * 不整合
    * サーバがInvokeMethodを受け取った時、前提とするオブジェクトのリビジョンとサーバ内のオブジェクトのリビジョンが違う
    * ピアがInvokeMethodを受け取った時、前提とするオブジェクトのリビジョンとピア内のオブジェクトのリビジョンが違う
  * 不整合を検出したらどうする？
    * PeerにInvokeMethod失敗を通知する？
* 同期
  * Eventual Consistencyは必須
  * j???

## ルーム管理

### メッセージ定義

### 入室時メッセージシーケンス
```mermaid
sequenceDiagram
  NewPeer->>Server: EnterRoom
  alt OK
    Server->>NewPeer: EnterRoomAllowed
    Server->>ExistingPeer: PeerEntered
  else NG
    Server->>NewPeer: EnterRoomDenied
  end
```

### 入室後
#### ピア同士の通信
```mermaid
sequenceDiagram
  Peer1->>Server: PeerToPeerMessage
  Server->>Peer1: PeerToPeerMessage
  Server->>Peer2: PeerToPeerMessage
```
#### プロファイル変更
```mermaid
sequenceDiagram
  Peer1->>Server: UpdateProfile
  Server->>Peer2: UpdateProfile
```


### 退室時
#### Peerからの退室通知
```mermaid
sequenceDiagram
  Peer1->>Server: LeaveRoom
  Server->>Peer1: LeaveRoomDone
  Server->>Peer2: PeerLeaved(peerId: string)
```
#### サーバ側での退室処理
```mermaid
sequenceDiagram
  Server->>Server: 退出の検知(切断、エラーなど)
  Server->>Peer1: RoomClosed
  Server->>Peer2: PeerLeaved(peerId: string)
```
#### サーバ側のシャットダウン
```mermaid
sequenceDiagram
  Server->>Server: ルームを閉じる処理
  Server->>Peer1: RoomClosed
  Server->>Peer2: RoomClosed
```

## オブジェクト管理メッセージシーケンス

### 入室後
```mermaid
sequenceDiagram
  Peer1->>Server: DefineFunction
  Peer1->>Server: DefineObject
  Peer1->>Server: InvokeMethod(InvokeFunction)
  Server->>Peer1: InvokeMethod(InvokeFunction)
  Server->>Peer2: InvokeMethod(InvokeFunction)
  Peer1->>Server: UpdateOjectState
  Server->>Peer1: UpdateOjectState
  Server->>Peer2: UpdateOjectState
```