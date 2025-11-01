# Madoiのアノテーション(デコレータ)


## 共通

Class Decorator|説明
-|-
`@ClassName(name: string)`|Madoiで管理する際のクラス名を指定する。トランスパイルでクラス名が失われる場合にこのデコレータでクラス名を与える。クラス名はサーバ側の管理画面でのオブジェクト一覧表示等に使用される。

## ルーム・ピア管理

Method Decorator|説明
-|-
`@BeforeEnterRoom()`|ルームに入る直前に実行されるメソッド。実行時の引数: `(selfPeerProfile: {[key: string]: any}, madoi: Madoi)`。
`@EnterRoomAllowed()`|ルームに入った直後に実行されるメソッド。実行時の引数: `(detail: EnterRoomAllowedDetail, madoi: Madoi)`。
`@EnterRoomDenied()`|ルームに入ることを拒否された際に実行されるメソッド。実行時の引数: `(detail: EnterRoomDeniedDetail, madoi: Madoi)`。
`@LeaveRoomDone()`|ルームから出た際に実行されるメソッド。実行時の引数: `(madoi: Madoi)`。
`@RoomProfileUpdated()`|ルームのプロファイルが更新された際に実行されるメソッド。実行時の引数: `(updates: RoomProfileUpdatedDetail, madoi: Madoi)`。
`@PeerEntered()`|ピア(他の参加者)が入室した際に実行されるメソッド。実行時の引数: `(detail: PeerEnteredDetail, madoi: Madoi)`。
`@PeerLeaved()`|ピアが退室した際に実行されるメソッド。実行時の引数: `(detail: PeerLeavedDetail, madoi: Madoi)`。
`@PeerProfileUdpated()`|ピアノプロファイルが更新された際に実行されるメソッド。実行時の引数: `(updates: PeerProfileUpdatedDetail, madoi: Madoi)`。


## メッセージ管理

Method Decorator|説明
-|-
`@UserMessageArrived(type: string)`|unicast, broadcast等で送信されたメッセージを受け取るメソッドを指定する。実行時の引数: `(detail: UserMessageDetail, madoi: Madoi)`。

## 分散共有オブジェクト

Madoiに登録されているオブジェクトのメソッドのみで、以下の指定が有効になる。

Method Decorator|説明
-|-
`@Distributed({serialized: boolean = true})`|実行を他のピアにも分散させるメソッドに指定する。このメソッドの実行は、同じルームに参加している他のピアにも送信され、実行される。serializedにtrue(デフォルト)が指定されているメソッドが複数実行される場合、全ピアで実行順が同じになる。Madoiサーバを利用している場合、メソッドが呼び出されると実行せず、サーバに送信、サーバから全ピアに送信、受信後に実行という処理になる。serializedがfalseに設定されている場合、メソッドが呼び出されると、実行されてから、他のピアに送信され、受信後に実行という処理になる。
`@ChangeState()`|実行によりオブジェクトの状態が変更されるメソッドに指定する。メソッドが実行されると、オブジェクトの状態が変更されたと見なされる。`@Distributed()`が指定されている場合は、メソッドの実行が記録される(新規参加ピアに実行履歴が送信される)。
`@GetState({minInterval: number, maxInterval: number})`|オブジェクトの状態を取得するメソッドに指定する。オブジェクトの状態が最後に変更されてからminIntervalミリ秒後、またはオブジェクトの状態が最初に変更されてからmaxIntervalミリ秒後に、madoiからメソッドが呼び出され、状態が取得され、記録される。この際、既に記録されている`@Distributed()`かつ`@ChangeState()`のメソッド実行は消去される。前記オブジェクトの状態が最初に変更された時間もクリアされる。
`@SetState()`|オブジェクトの状態を設定するメソッドに指定する。ルームへの入室直後に、ルームが管理しているオブジェクトの状態を設定するためにメソッドが実行される。
`@HostOnly()`|アプリケーションがホスト(最も参加順の早いピア)の場合のみ実行するメソッドに指定する。ホストでは無い場合、このメソッドの実行は無視される。

## React

Method Decorator|説明
-|-
`@SuppressRender()`|Reactのレンダリングを行わない。デフォルトでは、madoi-clientのアノテーション(デコレータ)が指定されたメソッドが実行されると、`@GetState()`を除いて、Reactのレンダリングプロセスが実行される。最初に`@GetState()`が指定されたメソッドが返す値が以前と同じかチェックされ、同じであればその後の処理は行われないが、`@SuppressRender()`を指定したメソッドではこの処理も行われない。
