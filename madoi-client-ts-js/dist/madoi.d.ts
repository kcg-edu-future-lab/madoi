export interface RoomInfo {
    id: string;
    profile: {
        [key: string]: any;
    };
}
export interface PeerInfo {
    id: string;
    order: number;
    profile: {
        [key: string]: any;
    };
}
interface EnterRoomAllowedDetail {
    room: RoomInfo;
    selfPeer: {
        id: string;
        order: number;
    };
    otherPeers: PeerInfo[];
}
interface EnterRoomDeniedDetail {
}
interface LeaveRoomDoneDetail {
}
interface RoomProfileUpdatedDetail {
    roomId: string;
    updates?: {
        [key: string]: any;
    };
    deletes?: string[];
}
interface PeerEnteredDetail {
    peer: PeerInfo;
}
interface PeerLeavedDetail {
    peerId: string;
}
interface PeerProfileUpdatedDetail {
    peerId: string;
    updates?: {
        [key: string]: any;
    };
    deletes?: string[];
}
interface MessageDetail {
    type: string;
    sender?: string;
    castType?: CastType;
    recipients?: string[];
    body: any;
}
interface ErrorDetail {
    error: any;
}
interface TypedCustomEvent<T extends TypedEventTarget<T>, D = any> extends CustomEvent<D> {
    currentTarget: T;
    detail: D;
}
export type TypedEventListener<T extends TypedEventTarget<T>, D = any> = (evt: TypedCustomEvent<T, D>) => void;
export interface TypedEventListenerObject<T extends TypedEventTarget<T>, D = any> extends EventListenerObject {
    handleEvent(object: TypedCustomEvent<T, D>): void;
}
export type TypedEventListenerOrEventListenerObject<T extends TypedEventTarget<T>, D = any> = TypedEventListener<T, D> | TypedEventListenerObject<T, D>;
declare class TypedEventTarget<T extends TypedEventTarget<T>> extends EventTarget {
}
type MadoiAddEventListenerOption = boolean | AddEventListenerOptions | undefined;
interface MadoiEventListeners {
    addEventListener(type: "enterRoomAllowed", callback: EnterRoomAllowedListener, options?: MadoiAddEventListenerOption): void;
    addEventListener(type: "enterRoomDenied", callback: EnterRoomDeniedListener, options?: MadoiAddEventListenerOption): void;
    addEventListener(type: "leaveRoomDone", callback: LeaveRoomDoneListener, options?: MadoiAddEventListenerOption): void;
    addEventListener(type: "roomProfileUpdated", callback: RoomProfileUpdatedListener, options?: MadoiAddEventListenerOption): void;
    addEventListener(type: "peerEntered", callback: PeerEnteredListener, options?: MadoiAddEventListenerOption): void;
    addEventListener(type: "peerProfileUpdated", callback: PeerProfileUpdatedListener, options?: MadoiAddEventListenerOption): void;
    addEventListener(type: "peerLeaved", callback: PeerLeavedListener, options?: MadoiAddEventListenerOption): void;
    addEventListener(type: "error", callback: ErrorListener, options?: MadoiAddEventListenerOption): void;
    removeEventListener(type: "enterRoomAllowed", callback: EnterRoomAllowedListener): void;
    removeEventListener(type: "enterRoomDenied", callback: EnterRoomDeniedListener): void;
    removeEventListener(type: "leaveRoomDone", callback: LeaveRoomDoneListener): void;
    removeEventListener(type: "peerProfileUpdated", callback: PeerProfileUpdatedListener): void;
    removeEventListener(type: "peerEntered", callback: PeerEnteredListener): void;
    removeEventListener(type: "peerLeaved", callback: PeerLeavedListener): void;
    removeEventListener(type: "peerProfileUpdated", callback: PeerProfileUpdatedListener): void;
    removeEventListener(type: "error", callback: ErrorListener): void;
}
declare class MadoiEventTarget<T extends MadoiEventTarget<T>> extends TypedEventTarget<T> {
    protected fire(type: "enterRoomAllowed", detail: EnterRoomAllowedDetail): void;
    protected fire(type: "enterRoomDenied", detail: EnterRoomDeniedDetail): void;
    protected fire(type: "leaveRoomDone", detail: LeaveRoomDoneDetail): void;
    protected fire(type: "roomProfileUpdated", detail: RoomProfileUpdatedDetail): void;
    protected fire(type: "peerEntered", detail: PeerEnteredDetail): void;
    protected fire(type: "peerLeaved", detail: PeerLeavedDetail): void;
    protected fire(type: "peerProfileUpdated", detail: PeerProfileUpdatedDetail): void;
    protected fire(type: "error", detail: ErrorDetail): void;
}
export type EnterRoomAllowedListener = TypedEventListenerOrEventListenerObject<Madoi, EnterRoomAllowedDetail> | null;
export type EnterRoomDeniedListener = TypedEventListenerOrEventListenerObject<Madoi, EnterRoomDeniedDetail> | null;
export type LeaveRoomDoneListener = TypedEventListenerOrEventListenerObject<Madoi, LeaveRoomDoneDetail> | null;
export type RoomProfileUpdatedListener = TypedEventListenerOrEventListenerObject<Madoi, RoomProfileUpdatedDetail> | null;
export type PeerEnteredListener = TypedEventListenerOrEventListenerObject<Madoi, PeerEnteredDetail> | null;
export type PeerLeavedListener = TypedEventListenerOrEventListenerObject<Madoi, PeerLeavedDetail> | null;
export type PeerProfileUpdatedListener = TypedEventListenerOrEventListenerObject<Madoi, PeerProfileUpdatedDetail> | null;
export type MessageListener = TypedEventListenerOrEventListenerObject<Madoi, MessageDetail> | null;
export type ErrorListener = TypedEventListenerOrEventListenerObject<Madoi, ErrorDetail> | null;
export declare function ShareClass(config?: {
    className?: string;
}): (target: any) => void;
export interface ShareConfig {
    type?: "beforeExec" | "afterExec";
    maxLog?: number;
    allowedTo?: string[];
    update?: {
        freq?: number;
        interpolateBy?: number;
        reckonUntil?: number;
    };
}
export declare const shareConfigDefault: ShareConfig;
export declare function Share(config?: ShareConfig): (target: any, name: string, descriptor: PropertyDescriptor) => void;
export interface GetStateConfig {
    maxInterval?: number;
    maxUpdates?: number;
}
export declare const getStateConfigDefault: GetStateConfig;
export declare function GetState(config?: GetStateConfig): (target: any, name: string, descriptor: PropertyDescriptor) => void;
export interface SetStateConfig {
}
export declare function SetState(config?: SetStateConfig): (target: any, name: string, descriptor: PropertyDescriptor) => void;
export interface HostOnlyConfig {
}
export declare function HostOnly(config?: HostOnlyConfig): (target: any, name: string, descriptor: PropertyDescriptor) => void;
export interface EnterRoomAllowedConfig {
}
export declare function EnterRoomAllowed(config?: EnterRoomAllowedConfig): (target: any, name: string, descriptor: PropertyDescriptor) => void;
export interface EnterRoomDeniedConfig {
}
export declare function EnterRoomDenied(config?: EnterRoomDeniedConfig): (target: any, name: string, descriptor: PropertyDescriptor) => void;
export interface LeaveRoomDoneConfig {
}
export declare function LeaveRoomDone(config?: LeaveRoomDoneConfig): (target: any, name: string, descriptor: PropertyDescriptor) => void;
export interface RoomProfileUpdatedConfig {
}
export declare function RoomProfileUpdated(config?: RoomProfileUpdatedConfig): (target: any, name: string, descriptor: PropertyDescriptor) => void;
export interface PeerEnteredConfig {
}
export declare function PeerEntered(config?: PeerEnteredConfig): (target: any, name: string, descriptor: PropertyDescriptor) => void;
export interface PeerLeavedConfig {
}
export declare function PeerLeaved(config?: PeerLeavedConfig): (target: any, name: string, descriptor: PropertyDescriptor) => void;
export interface PeerProfileUpdatedConfig {
}
export declare function PeerProfileUpdated(config?: PeerProfileUpdatedConfig): (target: any, name: string, descriptor: PropertyDescriptor) => void;
export type MethodConfig = {
    share: ShareConfig;
} | {
    hostOnly: HostOnlyConfig;
} | {
    getState: GetStateConfig;
} | {
    setState: SetStateConfig;
} | {
    enterRoomAllowed: EnterRoomAllowedConfig;
} | {
    enterRoomDenied: EnterRoomDeniedConfig;
} | {
    leaveRoomDone: LeaveRoomDoneConfig;
} | {
    roomProfileUpdated: RoomProfileUpdatedConfig;
} | {
    peerEntered: PeerEnteredConfig;
} | {
    peerLeaved: PeerLeavedConfig;
} | {
    peerProfileUpdated: PeerProfileUpdatedConfig;
};
export type CastType = "UNICAST" | "MULTICAST" | "BROADCAST" | "SELFCAST" | "OTHERCAST" | "PEERTOSERVER" | "SERVERTOPEER";
export interface Message {
    type: string;
    sender?: string;
    castType?: CastType;
    recipients?: string[];
    [name: string]: any;
}
export interface ServerToPeerMessage extends Message {
    sender: "__SERVER__";
    castType: "SERVERTOPEER";
    recipients: undefined;
}
export interface PeerToServerMessage extends Message {
    castType: "PEERTOSERVER";
    recipients: undefined;
}
export interface PeerToPeerMessage extends Message {
    castType: "UNICAST" | "MULTICAST" | "BROADCAST" | "OTHERCAST";
}
export interface BroadcastMessage extends PeerToPeerMessage {
    castType: "BROADCAST";
    recipients: undefined;
}
export interface BroadcastOrOthercastMessage extends PeerToPeerMessage {
    castType: "BROADCAST" | "OTHERCAST";
    recipients: undefined;
}
export interface Ping extends PeerToServerMessage {
    type: "Ping";
    body: object | undefined;
}
export declare function newPing(body?: undefined): Ping;
export interface Pong extends ServerToPeerMessage {
    type: "Pong";
    body: object | undefined;
}
export interface EnterRoomBody {
    roomProfile?: {
        [key: string]: string;
    };
    selfPeer?: PeerInfo;
}
export interface EnterRoom extends PeerToServerMessage, EnterRoomBody {
    type: "EnterRoom";
}
export declare function newEnterRoom(body: EnterRoomBody): EnterRoom;
export interface EnterRoomAllowed extends ServerToPeerMessage {
    type: "EnterRoomAllowed";
    room: RoomInfo;
    selfPeer: PeerInfo;
    otherPeers: PeerInfo[];
    histories: StoredMessageType[];
}
export interface EnterRoomDenied extends ServerToPeerMessage {
    type: "EnterRoomDenied";
}
export interface LeaveRoomBody {
}
export interface LeaveRoom extends PeerToServerMessage, LeaveRoomBody {
    type: "LeaveRoom";
}
export declare function newLeaveRoom(body: LeaveRoomBody): LeaveRoom;
export interface LeaveRoomDone extends ServerToPeerMessage {
    type: "LeaveRoomDone";
}
export interface UpdateRoomProfileBody {
    updates?: {
        [key: string]: string;
    };
    deletes?: string[];
}
export interface UpdateRoomProfile extends BroadcastMessage, UpdateRoomProfileBody {
    type: "UpdateRoomProfile";
}
export declare function newUpdateRoomProfile(body: UpdateRoomProfileBody): UpdateRoomProfile;
export interface PeerEntered extends ServerToPeerMessage {
    type: "PeerEntered";
    peer: PeerInfo;
}
export interface PeerLeaved extends ServerToPeerMessage {
    type: "PeerLeaved";
    peerId: string;
}
export interface UpdatePeerProfileBody {
    updates?: {
        [key: string]: string;
    };
    deletes?: string[];
}
export interface UpdatePeerProfile extends BroadcastMessage, UpdatePeerProfileBody {
    type: "UpdatePeerProfile";
}
export declare function newUpdatePeerProfile(body: UpdatePeerProfileBody): UpdatePeerProfile;
export interface MethodDefinition {
    methodId: number;
    name: string;
    config: MethodConfig;
}
export interface ObjectDefinition {
    objId: number;
    className: string;
    methods: MethodDefinition[];
}
export interface DefineObjectBody {
    definition: ObjectDefinition;
}
export interface DefineObject extends PeerToServerMessage, DefineObjectBody {
    type: "DefineObject";
}
export declare function newDefineObject(body: DefineObjectBody): DefineObject;
export interface FunctionDefinition {
    funcId: number;
    name: string;
    config: MethodConfig;
}
export interface DefineFunctionBody {
    definition: FunctionDefinition;
}
export interface DefineFunction extends PeerToServerMessage, DefineFunctionBody {
    type: "DefineFunction";
}
export declare function newDefineFunction(body: DefineFunctionBody): DefineFunction;
export interface InvokeMethodBody {
    objId?: number;
    objRevision?: number;
    methodId: number;
    args: any[];
}
export interface InvokeMethod extends BroadcastOrOthercastMessage, InvokeMethodBody {
    type: "InvokeMethod";
}
export declare function newInvokeMethod(castType: "BROADCAST" | "OTHERCAST", body: InvokeMethodBody): InvokeMethod;
export interface InvokeFunctionBody {
    funcId: number;
    args: any[];
}
export interface InvokeFunction extends BroadcastOrOthercastMessage, InvokeFunctionBody {
    type: "InvokeFunction";
}
export declare function newInvokeFunction(castType: "BROADCAST" | "OTHERCAST", body: InvokeFunctionBody): InvokeFunction;
export interface UpdateObjectStateBody {
    objId: number;
    state: string;
    revision: number;
}
export interface UpdateObjectState extends BroadcastMessage {
    type: "UpdateObjectState";
}
export declare function newUpdateObjectState(body: UpdateObjectStateBody): UpdateObjectState;
export interface CustomMessage extends Message {
    body: any;
}
export type UpstreamMessageType = Ping | EnterRoom | LeaveRoom | UpdateRoomProfile | UpdatePeerProfile | DefineObject | DefineFunction | InvokeMethod | InvokeFunction | UpdateObjectState;
export type DownStreamMessageType = Pong | EnterRoomAllowed | EnterRoomDenied | LeaveRoomDone | UpdateRoomProfile | PeerEntered | PeerLeaved | UpdatePeerProfile | InvokeMethod | InvokeFunction | UpdateObjectState | CustomMessage;
export type StoredMessageType = InvokeMethod | InvokeFunction | UpdateObjectState;
export type MethodAndConfigParam = {
    method: Function;
} & MethodConfig;
export declare class Madoi extends MadoiEventTarget<Madoi> implements MadoiEventListeners {
    private connecting;
    private interimQueue;
    private sharedFunctions;
    private sharedObjects;
    private getStateMethods;
    private setStateMethods;
    private enterRoomAllowedMethods;
    private enterRoomDeniedMethods;
    private leaveRoomDoneMethods;
    private roomProfileUpdatedMethods;
    private peerEnteredMethods;
    private peerLeavedMethods;
    private peerProfileUpdatedMethods;
    private promises;
    private objectModifications;
    private objectRevisions;
    private url;
    private ws;
    private room;
    private selfPeer;
    private peers;
    private currentSender;
    constructor(servicePath: string, selfPeer?: {
        id: string;
        profile: {
            [key: string]: string;
        };
    }, roomProfile?: {
        [key: string]: string;
    });
    getRoomProfile(): {
        [key: string]: any;
    };
    setRoomProfile(name: string, value: any): void;
    removeRoomProfile(name: string): void;
    getSelfPeerId(): string;
    getSelfPeerProfile(): {
        [key: string]: any;
    };
    setSelfPeerProfile(name: string, value: any): void;
    removeSelfPeerProfile(name: string): void;
    getCurrentSender(): PeerInfo | null | undefined;
    isCurrentSenderSelf(): boolean;
    close(): void;
    private sendPing;
    private handleOnOpen;
    private handleOnClose;
    private handleOnError;
    private handleOnMessage;
    private data;
    private systemMessageTypes;
    private isSystemMessageType;
    send(type: string, body: any, castType?: "UNICAST" | "MULTICAST" | "BROADCAST" | "SELFCAST" | "OTHERCAST" | "PEERTOSERVER"): void;
    unicast(type: string, body: any, recipient: string): void;
    multicast(type: string, body: any, recipients: string[]): void;
    broadcast(type: string, body: any): void;
    othercast(type: string, body: any): void;
    sendMessage(msg: Message): void;
    addReceiver(type: string, listener: MessageListener): void;
    removeReceiver(type: string, listener: MessageListener): void;
    private doSendMessage;
    register<T>(object: T, methodAndConfigs?: MethodAndConfigParam[]): T;
    registerFunction(func: Function, config?: MethodConfig): Function;
    private addSharedFunction;
    private addHostOnlyFunction;
    saveStates(): void;
    private applyInvocation;
}
export {};
