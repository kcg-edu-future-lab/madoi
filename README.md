# madoi-core

分散共有オブジェクトフレームワーク 'madoi' のコア部分。メッセージ配信処理のみを実装。

## 用語
|用語|説明|
|---|---|
|メッセージ(Message)|クライアントやサーバ間でやりとりされるデータ。JSON形式|
|ルーム(Room)|ピアが参加する、メッセージ配信の区切りとなるグループ。メッセージ配信はルーム内で完結する。|
|ピア(Peer)|クライアントを識別する単位。IDとプロファイル(name=valueのセット)を持ち、ルームに参加してメッセージ通信を行う|

## メッセージシーケンス

### 入室時
```mermaid
sequenceDiagram
  Peer1->>Server: EnterRoom(selfId?: string, roomSpec?: {}, profile?: {}
  alt OK
    Server->>Peer1: EnterRoomAllowed(self: {peerId: string, order: number}, peers: PeerInfo[], histories: (MethodInvoked|ObjectStateChanged)[])
    Server->>Peer2: PeerEntered(peerId: string, profile?: {})
  else NG
    Server->>Peer1: EnterRoomDenied
  end  
```

### 入室後
```mermaid
sequenceDiagram
  Peer1->>Server: UpdateProfile(peerId: string, updates?: {}, deletes?: string[])
  Server->>Peer2: PeerProfileUpdated(peerId: string, updates?: {}, deletes?: string[])
  Peer1->>Server: DefineFunction
  Peer1->>Server: DefineObject
  Peer1->>Server: DefineMethod
  Peer1->>Server: InvokeMethod
  Server->>Peer1: MethodInvoked
  Server->>Peer2: MethodInvoked
  Peer1->>Server: NotifyOjectState(objectId: string, state: {})
  Server->>Peer1: ObjectStateChanged
  Server->>Peer2: ObjectStateChanged  
```

### 退室時
```mermaid
sequenceDiagram
  Peer1->>Server: LeaveRoom
  Server->>Peer1: LeaveRoomAllowed
  Server->>Peer2: PeerLeaved(peerId: string)
```
```mermaid
sequenceDiagram
  Server->>Server: 特定のピアの退出処理
  Server->>Peer1: RoomClosed
  Server->>Peer2: PeerLeaved(peerId: string)
```
```mermaid
sequenceDiagram
  Server->>Server: ルームを閉じる処理
  Server->>Peer1: RoomClosed
  Server->>Peer2: RoomClosed
```

