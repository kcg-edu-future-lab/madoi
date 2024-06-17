//---- common classes ----
export interface RoomInfo{
	id: string;
	profile: {[key: string]: any};
}
export interface PeerInfo{
	id: string;
	order: number;
	profile: {[key: string]: any};
}

//---- events ----
interface EnterRoomAllowedDetail{
	room: RoomInfo;
	selfPeer: {id: string, order: number};
	otherPeers: PeerInfo[];
}
interface EnterRoomDeniedDetail{
	message: string;
}
interface LeaveRoomDoneDetail{
}
interface RoomProfileUpdatedDetail{
	updates?: {[key: string]: any};
	deletes?: string[];
}
interface PeerEnteredDetail{
	peer: PeerInfo;
}
interface PeerLeavedDetail{
	peerId: string;
}
interface PeerProfileUpdatedDetail{
	peerId: string;
	updates?: {[key: string]: any};
	deletes?: string[];
}
interface MessageDetail{
	type: string;
	sender?: string;
	castType?: CastType;
	recipients?: string[];
	body: any;
}
interface ErrorDetail{
	error: any;
}
interface TypedCustomEvent<T extends TypedEventTarget<T>, D = any>
extends CustomEvent<D>{
    currentTarget: T;
    detail: D;
}
export type TypedEventListener<T extends TypedEventTarget<T>, D = any>
    = (evt: TypedCustomEvent<T, D>) => void;
export interface TypedEventListenerObject<T extends TypedEventTarget<T>, D = any>
extends EventListenerObject{
    handleEvent(object: TypedCustomEvent<T, D>): void;
}
export type TypedEventListenerOrEventListenerObject<T extends TypedEventTarget<T>, D = any> =
    | TypedEventListener<T, D>
    | TypedEventListenerObject<T, D>;
type AnyEventListener<T extends TypedEventTarget<T>>
    = (evt: TypedCustomEvent<T, any>) => void;
class TypedEventTarget<T extends TypedEventTarget<T>>
extends EventTarget{
}
type MadoiAddEventListenerOption = boolean | AddEventListenerOptions | undefined;

interface MadoiEventListeners{
	addEventListener(type: "enterRoomAllowed", callback: EnterRoomAllowedListener,
		options?: MadoiAddEventListenerOption): void;
	addEventListener(type: "enterRoomDenied", callback: EnterRoomDeniedListener,
		options?: MadoiAddEventListenerOption): void;
	addEventListener(type: "leaveRoomDone", callback: LeaveRoomDoneListener,
		options?: MadoiAddEventListenerOption): void;
	addEventListener(type: "roomProfileUpdated", callback: RoomProfileUpdatedListener,
		options?: MadoiAddEventListenerOption): void;
	addEventListener(type: "peerEntered", callback: PeerEnteredListener,
		options?: MadoiAddEventListenerOption): void;
	addEventListener(type: "peerProfileUpdated", callback: PeerProfileUpdatedListener,
		options?: MadoiAddEventListenerOption): void;
	addEventListener(type: "peerLeaved", callback: PeerLeavedListener,
		options?: MadoiAddEventListenerOption): void;
	addEventListener(type: "error", callback: ErrorListener,
		options?: MadoiAddEventListenerOption): void;
	removeEventListener(type: "enterRoomAllowed", callback: EnterRoomAllowedListener): void;
	removeEventListener(type: "enterRoomDenied", callback: EnterRoomDeniedListener): void;
	removeEventListener(type: "leaveRoomDone", callback: LeaveRoomDoneListener): void;
	removeEventListener(type: "peerProfileUpdated", callback: PeerProfileUpdatedListener): void;
	removeEventListener(type: "peerEntered", callback: PeerEnteredListener): void;
	removeEventListener(type: "peerLeaved", callback: PeerLeavedListener): void;
	removeEventListener(type: "peerProfileUpdated", callback: PeerProfileUpdatedListener): void;
	removeEventListener(type: "error", callback: ErrorListener): void;
}
class MadoiEventTarget<T extends MadoiEventTarget<T>> extends TypedEventTarget<T>{
	protected fire(type: "enterRoomAllowed", detail: EnterRoomAllowedDetail): void;
	protected fire(type: "enterRoomDenied", detail: EnterRoomDeniedDetail): void;
	protected fire(type: "leaveRoomDone", detail: LeaveRoomDoneDetail): void;
	protected fire(type: "roomProfileUpdated", detail: RoomProfileUpdatedDetail): void;
	protected fire(type: "peerEntered", detail: PeerEnteredDetail): void;
	protected fire(type: "peerLeaved", detail: PeerLeavedDetail): void;
	protected fire(type: "peerProfileUpdated", detail: PeerProfileUpdatedDetail): void;
	protected fire(type: "error", detail: ErrorDetail): void;
	protected fire(type: string, detail: object){
		super.dispatchEvent(new CustomEvent(type, {detail}));
	}
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


//---- decorators ----

// Decorator
export function ShareClass(config: {className?: string} = {}){
	return (target: any) => {
		target.madoiClassConfig_ = config;
	};
}

// Decorator
export interface ShareConfig{
	type?: "beforeExec" | "afterExec"
	maxLog?: number
	allowedTo?: string[]
	update?: {freq?: number, interpolateBy?: number, reckonUntil?: number}
}
export const shareConfigDefault: ShareConfig = {
	type: "beforeExec", maxLog: 0, allowedTo: ["USER"]
};
export function Share(config: ShareConfig = shareConfigDefault) {
	const c = config;
	if(!c.type) c.type = "beforeExec";
	if(!c.maxLog) c.maxLog = 0;
    return (target: any, name: string, descriptor: PropertyDescriptor) => {
		const mc: MethodConfig = {share: c};
		target[name].madoiMethodConfig_ = mc;
    }
}

// Decorator
export interface GetStateConfig{
	maxInterval?: number
	maxUpdates?: number
}
export const getStateConfigDefault: GetStateConfig = {
	maxInterval: 5000
};
export function GetState(config: GetStateConfig = getStateConfigDefault){
	const c = config;
    return (target: any, name: string, descriptor: PropertyDescriptor) => {
		const mc: MethodConfig = {getState: c};
		target[name].madoiMethodConfig_ = mc;
    }
}

// Decorator
export interface SetStateConfig{
}
export function SetState(config: SetStateConfig = {}){
	const c = config;
    return (target: any, name: string, descriptor: PropertyDescriptor) => {
		const mc: MethodConfig = {setState: c};
		target[name].madoiMethodConfig_ = mc;
    }
}

// Decorator
export interface HostOnlyConfig{
}
export function HostOnly(config: HostOnlyConfig = {}){
	return (target: any, name: string, descriptor: PropertyDescriptor) => {
		const c = config;
		const mc: MethodConfig = {hostOnly: c};
		target[name].madoiMethodConfig_ = mc;
    }
}

// Decorator
export interface EnterRoomAllowedConfig{
}
export function EnterRoomAllowed(config: EnterRoomAllowedConfig = {}){
	const c = config;
	return (target: any, name: string, descriptor: PropertyDescriptor) => {
		const mc: MethodConfig = {enterRoomAllowed: c};
		target[name].madoiMethodConfig_ = mc;
    }
}

// Decorator
export interface EnterRoomDeniedConfig{
}
export function EnterRoomDenied(config: EnterRoomDeniedConfig = {}){
	const c = config;
	return (target: any, name: string, descriptor: PropertyDescriptor) => {
		const mc: MethodConfig = {enterRoomDenied: c};
		target[name].madoiMethodConfig_ = mc;
    }
}

// Decorator
export interface LeaveRoomDoneConfig{
}
export function LeaveRoomDone(config: LeaveRoomDoneConfig = {}){
	const c = config;
	return (target: any, name: string, descriptor: PropertyDescriptor) => {
		const mc: MethodConfig = {leaveRoomDone: c};
		target[name].madoiMethodConfig_ = mc;
    }
}

// Decorator
export interface RoomProfileUpdatedConfig{
}
export function RoomProfileUpdated(config: RoomProfileUpdatedConfig = {}){
	const c = config;
	return (target: any, name: string, descriptor: PropertyDescriptor) => {
		const mc: MethodConfig = {roomProfileUpdated: c};
		target[name].madoiMethodConfig_ = mc;
    }
}

// Decorator
export interface PeerEnteredConfig{
}
export function PeerEntered(config: PeerEnteredConfig = {}){
	const c = config;
	return (target: any, name: string, descriptor: PropertyDescriptor) => {
		const mc: MethodConfig = {peerEntered: c};
		target[name].madoiMethodConfig_ = mc;
    }
}

// Decorator
export interface PeerLeavedConfig{
}
export function PeerLeaved(config: PeerLeavedConfig = {}){
	const c = config;
	return (target: any, name: string, descriptor: PropertyDescriptor) => {
		const mc: MethodConfig = {peerLeaved: c};
		target[name].madoiMethodConfig_ = mc;
    }
}

// Decorator
export interface PeerProfileUpdatedConfig{
}
export function PeerProfileUpdated(config: PeerProfileUpdatedConfig = {}){
	const c = config;
	return (target: any, name: string, descriptor: PropertyDescriptor) => {
		const mc: MethodConfig = {peerProfileUpdated: c};
		target[name].madoiMethodConfig_ = mc;
    }
}

export type MethodConfig = 
	{share: ShareConfig} |
	{hostOnly: HostOnlyConfig} |
	{getState: GetStateConfig} |
	{setState: SetStateConfig} |
	{enterRoomAllowed: EnterRoomAllowedConfig} |
	{enterRoomDenied: EnterRoomDeniedConfig} |
	{leaveRoomDone: LeaveRoomDoneConfig} |
	{roomProfileUpdated: RoomProfileUpdatedConfig} |
	{peerEntered: PeerEnteredConfig} |
	{peerLeaved: PeerLeavedConfig} |
	{peerProfileUpdated: PeerProfileUpdatedConfig;
}


// ---- messages ----
export type CastType =
	"UNICAST" | "MULTICAST" | "BROADCAST" |
	"SELFCAST" | "OTHERCAST" | 
	"PEERTOSERVER" | "SERVERTOPEER";

export interface Message{
	type: string;
	sender?: string;
	castType?: CastType;
	recipients?: string[];
	[name: string]: any;
}

// サーバ側でCastTypeが固定されているメッセージ用のinterface。
export interface ServerToPeerMessage extends Message{
	sender: "__SERVER__";
	castType: "SERVERTOPEER";
	recipients: undefined;
}
export interface PeerToServerMessage extends Message{
	castType: "PEERTOSERVER";
	recipients: undefined;
}
const peerToServerMessageDefault = {
	castType: "PEERTOSERVER" as "PEERTOSERVER",
	recipients: undefined
};

export interface PeerToPeerMessage extends Message{
	castType: "UNICAST" | "MULTICAST" | "BROADCAST" | "OTHERCAST";
}

export interface BroadcastMessage extends PeerToPeerMessage{
	castType: "BROADCAST";
	recipients: undefined;
}
const broadcastMessageDefault = {
	castType: "BROADCAST" as "BROADCAST",
	recipients: undefined
};

export interface BroadcastOrOthercastMessage extends PeerToPeerMessage{
	castType: "BROADCAST" | "OTHERCAST";
	recipients: undefined;
}

export interface Ping extends PeerToServerMessage{
	type: "Ping";
	body: object | undefined;
}
export function newPing(body = undefined): Ping{
	return {
		type: "Ping",
		...peerToServerMessageDefault,
		body: body
	};
}

export interface Pong extends ServerToPeerMessage{
	type: "Pong";
	body: object | undefined;
}

export interface EnterRoomBody{
	roomProfile?: {[key: string]: string};
	selfPeer?: PeerInfo;
}
export interface EnterRoom extends PeerToServerMessage, EnterRoomBody{
	type: "EnterRoom";
}
export function newEnterRoom(body: EnterRoomBody): EnterRoom{
	return {
		type: "EnterRoom",
		...peerToServerMessageDefault,
		...body
	};
}

export interface EnterRoomAllowed extends ServerToPeerMessage{
	type: "EnterRoomAllowed";
	room: RoomInfo;
	selfPeer: PeerInfo;
	otherPeers: PeerInfo[];
	histories: StoredMessageType[];
}
export interface EnterRoomDenied extends ServerToPeerMessage{
	type: "EnterRoomDenied";
	message: string;
}

export interface LeaveRoomBody{
}
export interface LeaveRoom extends PeerToServerMessage, LeaveRoomBody{
	type: "LeaveRoom";
}
export function newLeaveRoom(body: LeaveRoomBody): LeaveRoom{
	return {
		type: "LeaveRoom",
		...peerToServerMessageDefault,
		...body
	};
}
export interface LeaveRoomDone extends ServerToPeerMessage{
	type: "LeaveRoomDone";
}

export interface UpdateRoomProfileBody{
	updates?: {[key: string]: string};
	deletes?: string[];
}
export interface UpdateRoomProfile extends BroadcastMessage, UpdateRoomProfileBody{
	type: "UpdateRoomProfile"
}
export function newUpdateRoomProfile(body: UpdateRoomProfileBody): UpdateRoomProfile{
	return {
		type: "UpdateRoomProfile",
		...broadcastMessageDefault,
		...body
	};
}

export interface PeerEntered extends ServerToPeerMessage{
	type: "PeerEntered";
	peer: PeerInfo;
}

export interface PeerLeaved extends ServerToPeerMessage{
	type: "PeerLeaved";
	peerId: string;
}

export interface UpdatePeerProfileBody{
	updates?: {[key: string]: string};
	deletes?: string[];
}
export interface UpdatePeerProfile extends BroadcastMessage, UpdatePeerProfileBody{
	type: "UpdatePeerProfile"
}
export function newUpdatePeerProfile(body: UpdatePeerProfileBody): UpdatePeerProfile{
	return {
		type: "UpdatePeerProfile",
		...broadcastMessageDefault,
		...body
	};
}

export interface MethodDefinition{
	methodId: number;
	name: string;
	config: MethodConfig;
}
export interface ObjectDefinition{
	objId: number;
	className: string;
	methods: MethodDefinition[];
}
export interface DefineObjectBody{
	definition: ObjectDefinition;
}
export interface DefineObject extends PeerToServerMessage, DefineObjectBody{
	type: "DefineObject";
}
export function newDefineObject(body: DefineObjectBody): DefineObject{
	return {
		type: "DefineObject",
		...peerToServerMessageDefault,
		...body
	}
}

export interface FunctionDefinition{
	funcId: number;
	name: string;
	config: MethodConfig;
}
export interface DefineFunctionBody{
	definition: FunctionDefinition;
}
export interface DefineFunction extends PeerToServerMessage, DefineFunctionBody{
	type: "DefineFunction";
}
export function newDefineFunction(body: DefineFunctionBody): DefineFunction{
    return {
        type: "DefineFunction",
		...peerToServerMessageDefault,
        ...body
    };
}

export interface InvokeMethodBody{
	objId?: number;
	objRevision?: number;  // メソッド実行前のリビジョン
	methodId: number;
	args: any[];
}
export interface InvokeMethod extends BroadcastOrOthercastMessage, InvokeMethodBody{
	type: "InvokeMethod";
}
export function newInvokeMethod(castType: "BROADCAST" | "OTHERCAST", body: InvokeMethodBody): InvokeMethod{
    return {
        type: "InvokeMethod",
		castType: castType,
		recipients: undefined,
        ...body
    };
}
export interface InvokeFunctionBody{
	funcId: number;
	args: any[];
}
export interface InvokeFunction extends BroadcastOrOthercastMessage, InvokeFunctionBody{
	type: "InvokeFunction";
}
export function newInvokeFunction(castType: "BROADCAST" | "OTHERCAST", body: InvokeFunctionBody): InvokeFunction{
    return {
        type: "InvokeFunction",
		castType: castType,
		recipients: undefined,
        ...body
    };
}

export interface UpdateObjectStateBody{
	objId: number;
	state: string;
	revision: number;
}
export interface UpdateObjectState extends BroadcastMessage{
	type: "UpdateObjectState";
}
export function newUpdateObjectState(body: UpdateObjectStateBody): UpdateObjectState{
    return {
        type: "UpdateObjectState",
		...broadcastMessageDefault,
        ...body
    };
}

export interface CustomMessage extends Message{
    body: any;
}

export type UpstreamMessageType =
	Ping |
	EnterRoom | LeaveRoom |
	UpdateRoomProfile | UpdatePeerProfile |
	DefineObject | DefineFunction |
	InvokeMethod | InvokeFunction | UpdateObjectState;
export type DownStreamMessageType =
	Pong |
	EnterRoomAllowed | EnterRoomDenied | LeaveRoomDone | UpdateRoomProfile |
	PeerEntered | PeerLeaved | UpdatePeerProfile |
	InvokeMethod | InvokeFunction | UpdateObjectState | CustomMessage;
export type StoredMessageType = InvokeMethod | InvokeFunction | UpdateObjectState;


// ---- madoi ----
export type MethodAndConfigParam = {method: Function} & MethodConfig;

export class Madoi extends MadoiEventTarget<Madoi> implements MadoiEventListeners{
	private connecting: boolean = false;

	private interimQueue: Array<object>;

	// annotated methods
	private sharedFunctions: Function[] = [];
	private sharedObjects: object[] = []
	private getStateMethods = new Map<number, {method: Function, config: GetStateConfig, lastGet: number}>();
	private setStateMethods = new Map<number, Function>(); // objectId -> @SetState method
	private enterRoomAllowedMethods = new Map<number, (detail: EnterRoomAllowedDetail)=>void>();
	private enterRoomDeniedMethods = new Map<number, (detail: EnterRoomDeniedDetail)=>void>();
	private leaveRoomDoneMethods = new Map<number, ()=>void>();
	private roomProfileUpdatedMethods = new Map<number, (detail: RoomProfileUpdatedDetail)=>void>();
	private peerEnteredMethods = new Map<number, (detail: PeerEnteredDetail)=>void>();
	private peerLeavedMethods = new Map<number, (detail: PeerLeavedDetail)=>void>();
	private peerProfileUpdatedMethods = new Map<number, (detail: PeerProfileUpdatedDetail)=>void>();

	private promises: any = {};
	private objectModifications = new Map<number, number>();  // objectId -> modification (method invoked) count
	private objectRevisions = new Map<number, number>();  // objectId -> revision (sum of modification count)
	private url: string;
	private ws: WebSocket | null = null;
	private room: RoomInfo = {id: "", profile: {}};
	private selfPeer: PeerInfo;
	private peers = new Map<string, PeerInfo>();
	private currentSender: string | null = null;

	constructor(servicePath: string, selfPeer?: {id: string, profile: {[key: string]: string}}, roomProfile?: {[key: string]: string}){
		super();
		this.selfPeer = selfPeer ? {...selfPeer, order: -1} : {id: "", order: -1, profile: {}};
		this.interimQueue = new Array();
		this.doSendMessage(newEnterRoom({
				roomProfile: roomProfile, selfPeer: {id: "", profile: {}, ...selfPeer, order: -1}
				}));
		if(servicePath.match(/^wss?:\/\//)){
			this.url = `${servicePath}`;
		} else{
			const p = (document.querySelector("script[src$='madoi.js']") as HTMLScriptElement).src.split("\/", 5);
			const contextUrl = (p[0] == "http:" ? "ws:" : "wss:") + "//" + p[2] + "/" + p[3];
			this.url = `${contextUrl}/rooms/${servicePath}`;
		}

		this.ws = new WebSocket(this.url);
		this.ws.onopen = e => this.handleOnOpen(e);
		this.ws.onclose = e => this.handleOnClose(e);
		this.ws.onerror = e => this.handleOnError(e);
		this.ws.onmessage = e => this.handleOnMessage(e);

		setInterval(()=>{this.saveStates();}, 1000);
		setInterval(()=>{this.sendPing();}, 30000);
	}

	getRoomProfile(){
		return this.room?.profile;
	}

	setRoomProfile(name: string, value: any){
		const m: {[key: string]: any} = {};
		m[name] = value;
		this.sendMessage(newUpdateRoomProfile(
			{updates: m}
		));
	}

	removeRoomProfile(name: string){
		this.sendMessage(newUpdateRoomProfile(
			{deletes: [name]}
		));
	}

	getSelfPeerId(){
		return this.selfPeer?.id;
	}

	getSelfPeerProfile(){
		return this.selfPeer.profile;
	}

	setSelfPeerProfile(name: string, value: any){
		this.selfPeer.profile[name] = value;
		const m: {[key: string]: any} = {};
		m[name] = value;
		this.sendMessage(newUpdatePeerProfile(
			{updates: m}
		));
	}

	removeSelfPeerProfile(name: string){
		delete this.selfPeer.profile[name];
		this.sendMessage(newUpdatePeerProfile(
			{deletes: [name]}
		));
	}

	getCurrentSender(){
		if(!this.currentSender) return null;
		return this.peers.get(this.currentSender);
	}

	isCurrentSenderSelf(){
		return this.currentSender === this.selfPeer.id;
	}

	close(){
		this.ws?.close();
	}

	private sendPing(){
		this.ws?.send(JSON.stringify(newPing()));
	}

	private handleOnOpen(e: Event){
		this.connecting = true;
		for(let m of this.interimQueue){
			this.ws?.send(JSON.stringify(m));
		}
		this.interimQueue = [];
	}

	private handleOnClose(e: CloseEvent){
		console.debug(`websocket closed because: ${e.reason}.`);
		this.connecting = false;
		this.ws = null;
	}

	private handleOnError(e: Event){
	}

	private handleOnMessage(e: MessageEvent){
		const msg = JSON.parse(e.data);
		this.currentSender = msg.sender;
		this.data(msg);
	}

	private data(msg: DownStreamMessageType){
		if(msg.type == "Pong"){
		} else if(msg.type === "EnterRoomAllowed"){
			const m: EnterRoomAllowedDetail = msg as EnterRoomAllowed;
			for(const [_, f] of this.enterRoomAllowedMethods){
				f(m);
			}
			this.room = msg.room;
			this.selfPeer.order = msg.selfPeer.order;
			this.peers.set(m.selfPeer.id, {...m.selfPeer, profile: this.selfPeer.profile});
			for(const p of m.otherPeers){
				this.peers.set(p.id, p);
			}
			this.fire("enterRoomAllowed", m);
			if(msg.histories) for(const h of msg.histories){
				this.data(h);
			}
		} else if(msg.type === "EnterRoomDenied"){
			const m = msg as EnterRoomDenied;
			const d: EnterRoomDeniedDetail = m;
			for(const [_, f] of this.enterRoomDeniedMethods){
				f(d);
			}
			this.fire("enterRoomDenied", d);
		} else if(msg.type == "LeaveRoomDone"){
			for(const [_, f] of this.leaveRoomDoneMethods){
				f();
			}
			this.fire("leaveRoomDone", {});
		} else if(msg.type === "UpdateRoomProfile"){
			const m = msg as UpdateRoomProfile;
			if(msg.updates) for(const [key, value] of Object.entries(msg.updates)) {
				this.room.profile[key] = value;
			}
			if(msg.deletes) for(const key of msg.deletes){
				delete this.room.profile[key];
			}
			for(const [_, f] of this.roomProfileUpdatedMethods){
				f(m);
			}
			this.fire("roomProfileUpdated", m);
		} else if(msg.type === "PeerEntered"){
			const m: PeerEnteredDetail = msg as PeerEntered;
			this.peers.set(m.peer.id, m.peer);
			for(const [_, f] of this.peerEnteredMethods){
				f(m);
			}
			this.fire("peerEntered", {peer: m.peer});
		} else if(msg.type === "PeerLeaved"){
			this.peers.delete(msg.peerId);
			for(const [_, f] of this.peerLeavedMethods){
				f(msg.peerId);
			}
			this.fire("peerLeaved", msg.peerId);
		} else if(msg.type === "UpdatePeerProfile"){
			const p = this.peers.get(msg.sender!);
			if(msg.sender && p){
				if(msg.updates) for(const [key, value] of Object.entries(msg.updates)) {
					p.profile[key] = value;
				}
				if(msg.deletes) for(const key of msg.deletes){
					delete p.profile[key];
				}
				const v: PeerProfileUpdatedDetail = {...msg, peerId: msg.peerId};
				for(const [_, f] of this.peerProfileUpdatedMethods){
					f(v);
				}
				this.fire("peerProfileUpdated", v);
			}
		} else if(msg.type === "InvokeMethod"){
			if(msg.objId){
				// check consistency?
				const rev = this.objectRevisions.get(msg.objId);
				this.objectRevisions.set(msg.objId, (rev || 0) + 1);
			}
			const f = this.sharedFunctions[msg.methodId];
			if(f){
                const ret = this.applyInvocation(f, msg.args);
                if(ret instanceof Promise){
                    ret.then(()=>{
                        this.promises[msg.methodId]["resolve"].apply(null, arguments);
                    }).catch(()=>{
                        this.promises[msg.methodId]["reject"].apply(null, arguments);
                    });
                }
            } else {
				console.warn("no suitable method for ", msg);
			}
		} else if(msg.type === "InvokeFunction"){
			const f = this.sharedFunctions[msg.funcId];
			if(f){
                const ret = this.applyInvocation(f, msg.args);
                if(ret instanceof Promise){
                    ret.then(()=>{
                        this.promises[msg.methodId]["resolve"].apply(null, arguments);
                    }).catch(()=>{
                        this.promises[msg.methodId]["reject"].apply(null, arguments);
                    });
                }
            } else {
				console.warn("no suitable function for ", msg);
			}
		} else if(msg.type === "UpdateObjectState"){
			const f = this.setStateMethods.get(msg.objId);
			if(f){
				f(JSON.parse(msg.state), msg.revision);
			}
			this.objectRevisions.set(msg.objId, msg.revision);
		} else if(msg.type){
			this.dispatchEvent(new CustomEvent(msg.type, {detail: msg}));
		} else{
			console.log("Unknown message type.", msg);
		}
	}

	private systemMessageTypes = [
		"Ping", "Pong",
		"EnterRoom", "EnterRoomAllowed", "EnterRoomDenied",
		"LeaveRoom", "LeaveRoomDone", "UpdateRoomProfile",
		"PeerArrived", "PeerLeaved", "UpdatePeerProfile",
		"DefineFunction", "DefineObject", 
		"InvokeFunction", "UpdateObjectState", "InvokeMethod"
	];
	private isSystemMessageType(type: string){
		return type in this.systemMessageTypes;
	}

	send(type: string, body: any,
		castType: "UNICAST" | "MULTICAST" | "BROADCAST" |
			"SELFCAST" | "OTHERCAST" | "PEERTOSERVER" = "BROADCAST"
	){
		if(!this.ws) return;
		this.sendMessage({
			type: type,
			castType: castType,
			body: body
		});
	}

	unicast(type: string, body: any, recipient: string){
		this.sendMessage({
			type: type,
			castType: "UNICAST",
			recipients: [recipient],
			body: body
		});
	}

	multicast(type: string, body: any, recipients: string[]){
		this.sendMessage({
			type: type,
			castType: "MULTICAST",
			recipients: recipients,
			body: body
		});
	}

	broadcast(type: string, body: any){
		this.sendMessage({
			type: type,
			castType: "BROADCAST",
			body: body
		});
	}

	othercast(type: string, body: any){
		this.sendMessage({
			type: type,
			castType: "OTHERCAST",
			body: body
		});
	}

	sendMessage(msg: Message){
		if(this.isSystemMessageType(msg.type))
			throw new Error("システムメッセージは送信できません。");
		this.doSendMessage(msg);
	}

	addReceiver(type: string, listener: MessageListener){
		if(this.isSystemMessageType(type))
			throw new Error("システムメッセージのレシーバは登録できません。");
		this.addEventListener(type, listener as EventListener);
	}

	removeReceiver(type: string, listener: MessageListener){
		this.removeEventListener(type, listener as EventListener);
	}


	private doSendMessage(msg: Message){
		if(this.connecting){
			this.ws?.send(JSON.stringify(msg));
		} else{
			this.interimQueue.push(msg);
		}
	}

	register<T>(object: T, methodAndConfigs: MethodAndConfigParam[] = []): T{
		if(!this.ws) return object;
		const obj = object as any;
		if(obj.madoiObjectId_){
			console.warn("Ignore object registration because it's already registered.");
			return object;
		}
		let className = obj.constructor.name;
		if(obj.__proto__.constructor.madoiClassConfig_){
			className = obj.__proto__.constructor.madoiClassConfig_.className;
		}
		// 共有オブジェクトのidを確定
		const objId = this.sharedObjects.length;
		this.sharedObjects.push(obj);
		obj.madoiObjectId_ = objId;

		// コンフィグを集める
		const methods = new Array<Function>();
		const methodDefinitions = new Array<MethodDefinition>();
		const methodToIndex = new Map<string, number>();
			// デコレータから
		Object.getOwnPropertyNames(obj.__proto__).forEach(methodName => {
			const f = obj[methodName];
			if(typeof(f) != "function") return;
			if(!f.madoiMethodConfig_) return;
			const cfg: MethodConfig = f.madoiMethodConfig_;
			const mi = methods.length;
			methodToIndex.set(methodName, mi);
			methods.push(f);
			methodDefinitions.push({methodId: mi, name: methodName, config: cfg});
			console.debug(`add config ${className}.${methodName}=${JSON.stringify(cfg)} from decorator`);
		});
			// 引数から
		for(const mc of methodAndConfigs){
			const f = mc.method;
			const c: MethodConfig = mc;
			const methodName = f.name;
			if("share" in c){ // デフォルト値チェック
				if(!c.share.type) c.share.type = shareConfigDefault.type;
				if(!c.share.maxLog) c.share.maxLog = shareConfigDefault.maxLog;
			} else if("hostOnly" in c){
			} else if("getState" in c){
				if(!c.getState.maxInterval) c.getState.maxInterval = getStateConfigDefault.maxInterval;
			} else if("setState" in c){
			} else if("enterRoomAllowed" in c){
			} else if("enterRoomDenied" in c){
			} else if("leaveRoomDone" in c){
			} else if("peerEntered" in c){
			} else if("peerLeaved" in c){
			} else{
				continue;
			}
			const mi = methodToIndex.get(methodName);
			if(typeof mi === "undefined"){
				// 追加
				const mi = methods.length;
				methodToIndex.set(methodName, mi);
				methods.push(f);
				methodDefinitions.push({methodId: mi, name: mc.method.name, config: c});
				console.debug(`add config ${className}.${methodName}=${JSON.stringify(mc)} from argument`);
			} else{
				// 既にあれば設定を置き換え
				methodDefinitions[mi].config = mc;
				console.debug(`replace config ${className}.${methodName}=${JSON.stringify(mc)} from argument`);
			}
		}

		// 集めたコンフィグ内のメソッドに応じて登録や置き換え処理を行う。
		for(let i = 0; i < methods.length; i++){
			const f = methods[i];
			const mc = methodDefinitions[i];
			const c = mc.config;
			if("share" in c){
				// @Shareの場合はメソッドを置き換え
	            const [fi, newf] = this.addSharedFunction(
					f.bind(obj),
					c.share,
					objId);
				mc.methodId = fi;
				this.objectModifications.set(objId, 0);
				this.objectRevisions.set(objId, 0);
				const self = this;
				obj[f.name] = function(){
					const omc = self.objectModifications.get(objId)!;
					self.objectModifications.set(objId, omc + 1);
					const or = self.objectRevisions.get(objId)!;
					self.objectRevisions.set(objId, or + 1);
					return newf.apply(null, arguments);
				};
			} else if("hostOnly" in c){
				// @HostOnlyの場合はメソッドを置き換え
				const newf = this.addHostOnlyFunction(
					f.bind(obj), c.hostOnly);
				obj[f.name] = function(){
					return newf.apply(null, arguments);
				}
			} else if("getState" in c){
				// @GetStateの場合はメソッドを登録
				this.getStateMethods.set(objId, {method: f.bind(obj),
					config: c.getState, lastGet: 0});
			} else if("setState" in c){
				// @SetStateの場合はメソッドを登録
				this.setStateMethods.set(objId, f.bind(obj));
			} else if("enterRoomAllowed" in c){
				// @EnterRoomAllowedの場合はメソッドを登録
				this.enterRoomAllowedMethods.set(objId, f.bind(obj));
			} else if("enterRoomDenied" in c){
				// @EnterRoomDeniedの場合はメソッドを登録
				this.enterRoomDeniedMethods.set(objId, f.bind(obj));
			} else if("leaveRoomDone" in c){
				// @LeaveRoomDoneの場合はメソッドを登録
				this.leaveRoomDoneMethods.set(objId, f.bind(obj));
			} else if("peerEntered" in c){
				// @PeerEnteredの場合はメソッドを登録
				this.peerEnteredMethods.set(objId, f.bind(obj));
			} else if("peerProfileUpdated" in c){
				// @PeerProfileUpdatedの場合はメソッドを登録
				this.peerProfileUpdatedMethods.set(objId, f.bind(obj))
			} else if("peerLeaved" in c){
				// @PeerLeavedの場合はメソッドを登録
				this.peerLeavedMethods.set(objId, f.bind(obj))
			}
		}
		const msg = newDefineObject({
			definition: {
				objId: objId,
				className: className,
				methods: methodDefinitions
			}
		});
		this.doSendMessage(msg);
		return object;
	}

	registerFunction(func: Function, config: MethodConfig = {share: {}}){
		if("hostOnly" in config){
			return this.addHostOnlyFunction(func, config);
		} else if("share" in config){
			// デフォルト値チェック
			if(!config.share.type) config.share.type = shareConfigDefault.type;
			if(!config.share.maxLog) config.share.maxLog = shareConfigDefault.maxLog;

			const funcName = func.name;
			const [fid, f] = this.addSharedFunction(func, config.share);
			const ret = function(){
				return f.apply(null, arguments);
			};
			this.doSendMessage(newDefineFunction({
				definition: {
					funcId: fid,
					name: funcName,
					config: config
				}
			}));
			return ret;
		}
		return func;
	}

	private addSharedFunction(f: Function, config: ShareConfig, objectIndex?: number): [number, Function]{
		const index = this.sharedFunctions.length;
		this.sharedFunctions.push(f);
        this.promises[index] = {};
        this.promises[index]["promise"] = new Promise((resolve, reject)=>{
            this.promises[index]["resolve"] = resolve;
            this.promises[index]["reject"] = reject;
        });
		const self = this;
		return [index, function(){
			if(self.ws === null){
				if(f) return f.apply(null, arguments);
			} else{
                let ret = null;
				let castType: CastType = "BROADCAST";
				if(config.type === "afterExec"){
                    ret = f.apply(null, arguments);
					castType = "OTHERCAST";
				}
				if(objectIndex !== undefined){
					self.sendMessage(newInvokeMethod(
						castType, {
							objId: objectIndex,
							methodId: index,
							args: Array.from(arguments)
						}
					));
				} else{
					self.sendMessage(newInvokeFunction(
						castType, {
							funcId: index,
							args: Array.from(arguments)
						}
					));
				}
                return (ret != null) ? ret : self.promises[index]["promise"];
			}
		}];
	}

	private addHostOnlyFunction(f: Function, config: HostOnlyConfig): Function{
		const self = this;
		return function(){
			// orderが最も小さければ実行。そうでなければ無視
			let minOrder = self.selfPeer.order;
			for(const p of self.peers.values()){
				if(minOrder > p.order)
					minOrder = p.order;
			}
			if(self.selfPeer.order === minOrder){
				f.apply(null, arguments);
			}
		};
	}

	public saveStates(){
		if(!this.ws || !this.connecting) return;
		for(let [objId, count] of this.objectModifications){
			if(count == 0) continue;
			const info = this.getStateMethods.get(objId);
			if(!info) continue;
			const curTick = performance.now();
			if(info.config.maxUpdates && info.config.maxUpdates <= count ||
				info.config.maxInterval && info.config.maxInterval <= (curTick - info.lastGet)){
				this.doSendMessage(newUpdateObjectState({
					objId: objId,
					state: JSON.stringify(info.method()),
					revision: this.objectRevisions.get(objId)!
					}));
				info.lastGet = curTick;
				this.objectModifications.set(objId, 0);
				console.log(`state saved: ${objId}`)
			}
		}
	}

	private applyInvocation(method: Function, args: any[]){
		return method.apply(null, args);
	}
}
