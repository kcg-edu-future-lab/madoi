var E = Object.defineProperty;
var j = (i, l, e) => l in i ? E(i, l, { enumerable: !0, configurable: !0, writable: !0, value: e }) : i[l] = e;
var a = (i, l, e) => j(i, typeof l != "symbol" ? l + "" : l, e);
class C extends EventTarget {
  dispatchCustomEvent(l, e) {
    return super.dispatchEvent(new CustomEvent(l, { detail: e }));
  }
}
const v = {
  sender: "__PEER__",
  castType: "PEERTOSERVER",
  recipients: void 0
}, w = {
  sender: "__PEER__",
  castType: "BROADCAST",
  recipients: void 0
}, O = {
  sender: "__PEER__",
  recipients: void 0
};
function _(i = void 0) {
  return {
    type: "Ping",
    ...v,
    body: i
  };
}
function I(i) {
  return {
    type: "EnterRoom",
    ...v,
    ...i
  };
}
function L(i) {
  return {
    type: "LeaveRoom",
    ...v,
    ...i
  };
}
function P(i) {
  return {
    type: "UpdateRoomProfile",
    ...w,
    ...i
  };
}
function S(i) {
  return {
    type: "UpdatePeerProfile",
    ...w,
    ...i
  };
}
function A(i) {
  return {
    type: "DefineFunction",
    ...v,
    ...i
  };
}
function D(i) {
  return {
    type: "DefineObject",
    ...v,
    ...i
  };
}
function T(i, l) {
  return {
    type: "InvokeFunction",
    castType: i,
    ...O,
    ...l
  };
}
function $(i) {
  return {
    type: "UpdateObjectState",
    ...v,
    ...i
  };
}
function U(i, l) {
  return {
    type: "InvokeMethod",
    castType: i,
    ...O,
    ...l
  };
}
function m(i) {
  return (l, e, t) => {
    l[e].madoiMethodConfig_ = {
      ...l[e].madoiMethodConfig_ ? l[e].madoiMethodConfig_ : {},
      ...i
    };
  };
}
function F(i) {
  return (l) => {
    l.madoiClassConfig_ = { className: i };
  };
}
const b = {
  serialized: !0
};
function N(i = b) {
  return m({ distributed: i });
}
function x() {
  return m({ changeState: {} });
}
const R = {
  maxInterval: 5e3,
  minInterval: 3e3
};
function H(i = R) {
  return m({ getState: i });
}
function J() {
  return m({ setState: {} });
}
function z() {
  return m({ hostOnly: {} });
}
function B() {
  return m({ beforeEnterRoom: {} });
}
function Q() {
  return m({ enterRoomAllowed: {} });
}
function q() {
  return m({ enterRoomDenied: {} });
}
function G() {
  return m({ leaveRoomDone: {} });
}
function V() {
  return m({ roomProfileUpdated: {} });
}
function W() {
  return m({ peerEntered: {} });
}
function K() {
  return m({ peerLeaved: {} });
}
function X() {
  return m({ peerProfileUpdated: {} });
}
function Y(i) {
  return m({ userMessageArrived: { type: i } });
}
class Z extends C {
  constructor(e, t, s, o) {
    super();
    a(this, "connecting", !1);
    a(this, "interimQueue");
    a(this, "distributedFuncs", /* @__PURE__ */ new Map());
    a(this, "shareObjects", /* @__PURE__ */ new Map());
    a(this, "shareOrNotifyMethods", /* @__PURE__ */ new Map());
    // annotated methods
    a(this, "getStateMethods", /* @__PURE__ */ new Map());
    a(this, "setStateMethods", /* @__PURE__ */ new Map());
    // objectId -> @SetState method
    a(this, "beforeEnterRoomMethods", /* @__PURE__ */ new Map());
    a(this, "enterRoomAllowedMethods", /* @__PURE__ */ new Map());
    a(this, "enterRoomDeniedMethods", /* @__PURE__ */ new Map());
    a(this, "leaveRoomDoneMethods", /* @__PURE__ */ new Map());
    a(this, "roomProfileUpdatedMethods", /* @__PURE__ */ new Map());
    a(this, "peerEnteredMethods", /* @__PURE__ */ new Map());
    a(this, "peerLeavedMethods", /* @__PURE__ */ new Map());
    a(this, "peerProfileUpdatedMethods", /* @__PURE__ */ new Map());
    a(this, "userMessageArrivedMethods", []);
    a(this, "url");
    a(this, "ws", null);
    a(this, "room", { id: "", spec: { maxLog: 1e3 }, profile: {} });
    a(this, "selfPeer", { id: "", order: -1, profile: {} });
    a(this, "peers", /* @__PURE__ */ new Map());
    a(this, "currentSenderId", null);
    a(this, "systemMessageTypes", [
      "Ping",
      "Pong",
      "EnterRoom",
      "EnterRoomAllowed",
      "EnterRoomDenied",
      "LeaveRoom",
      "LeaveRoomDone",
      "UpdateRoomProfile",
      "PeerArrived",
      "PeerLeaved",
      "UpdatePeerProfile",
      "DefineFunction",
      "DefineObject",
      "InvokeFunction",
      "UpdateObjectState",
      "InvokeMethod"
    ]);
    o && (this.room = { ...this.room, ...o }), s && (this.selfPeer = { ...this.selfPeer, ...s, order: -1 }), this.interimQueue = new Array();
    const n = e.indexOf("?") != -1 ? "&" : "?";
    if (e.match(/^wss?:\/\//))
      this.url = `${e}${n}authToken=${t}`, this.room.id = e.split("rooms/")[1].split("?")[0];
    else {
      const r = document.querySelector("script[src$='madoi.js']").src.split("/", 5), f = (r[0] == "http:" ? "ws:" : "wss:") + "//" + r[2] + "/" + r[3];
      this.url = `${f}/rooms/${e}${n}authToken=${t}`, this.room.id = e;
    }
    this.ws = new WebSocket(this.url), this.ws.onopen = (r) => this.handleOnOpen(r), this.ws.onclose = (r) => this.handleOnClose(r), this.ws.onerror = (r) => this.handleOnError(r), this.ws.onmessage = (r) => this.handleOnMessage(r), setInterval(() => {
      this.saveStates();
    }, 1e3), setInterval(() => {
      this.sendPing();
    }, 3e4);
  }
  getRoomId() {
    return this.room.id;
  }
  getRoomProfile() {
    var e;
    return (e = this.room) == null ? void 0 : e.profile;
  }
  setRoomProfile(e, t) {
    const s = {};
    s[e] = t, this.sendMessage(P(
      { updates: s }
    ));
  }
  removeRoomProfile(e) {
    this.sendMessage(P(
      { deletes: [e] }
    ));
  }
  getSelfPeerId() {
    var e;
    return (e = this.selfPeer) == null ? void 0 : e.id;
  }
  getSelfPeerProfile() {
    return this.selfPeer.profile;
  }
  updateSelfPeerProfile(e, t) {
    this.selfPeer.profile[e] = t;
    const s = {};
    s[e] = t, this.sendMessage(S(
      { updates: s }
    ));
    const o = { updates: s, peerId: this.selfPeer.id };
    for (const [n, r] of this.peerProfileUpdatedMethods)
      r(o, this);
    this.dispatchCustomEvent("peerProfileUpdated", o);
  }
  removeSelfPeerProfile(e) {
    delete this.selfPeer.profile[e], this.sendMessage(S(
      { deletes: [e] }
    ));
    const t = { deletes: [e], peerId: this.selfPeer.id };
    for (const [s, o] of this.peerProfileUpdatedMethods)
      o(t, this);
    this.dispatchCustomEvent("peerProfileUpdated", t);
  }
  isMessageProcessing() {
    return this.currentSenderId !== null;
  }
  getCurrentSender() {
    return this.currentSenderId === null ? null : this.peers.get(this.currentSenderId);
  }
  isCurrentSenderSelf() {
    return this.currentSenderId === this.selfPeer.id;
  }
  close() {
    var e;
    (e = this.ws) == null || e.close(), this.ws = null;
  }
  sendPing() {
    var e;
    (e = this.ws) == null || e.send(JSON.stringify(_()));
  }
  handleOnOpen(e) {
    var t;
    this.connecting = !0;
    for (const [s, o] of this.beforeEnterRoomMethods)
      o(this.selfPeer.profile, this);
    this.doSendMessage(I({ room: this.room, selfPeer: this.selfPeer }));
    for (let s of this.interimQueue)
      (t = this.ws) == null || t.send(JSON.stringify(s));
    this.interimQueue = [];
  }
  handleOnClose(e) {
    console.debug(`websocket closed because: ${e.reason}.`), this.connecting = !1, this.ws = null;
  }
  handleOnError(e) {
  }
  handleOnMessage(e) {
    const t = JSON.parse(e.data);
    this.currentSenderId = t.sender;
    try {
      this.data(t);
    } finally {
      this.currentSenderId = null;
    }
  }
  data(e) {
    if (e.type != "Pong") if (e.type === "EnterRoomAllowed") {
      const t = e;
      for (const [s, o] of this.enterRoomAllowedMethods)
        o(t, this);
      this.room = e.room, this.selfPeer.order = e.selfPeer.order, this.peers.set(t.selfPeer.id, { ...t.selfPeer, profile: this.selfPeer.profile });
      for (const s of t.otherPeers)
        this.peers.set(s.id, s);
      if (this.dispatchCustomEvent("enterRoomAllowed", t), e.histories) for (const s of e.histories)
        this.data(s);
    } else if (e.type === "EnterRoomDenied") {
      const s = e;
      for (const [o, n] of this.enterRoomDeniedMethods)
        n(s, this);
      this.dispatchCustomEvent("enterRoomDenied", s);
    } else if (e.type == "LeaveRoomDone") {
      for (const [t, s] of this.leaveRoomDoneMethods)
        s(this);
      this.dispatchCustomEvent("leaveRoomDone");
    } else if (e.type === "UpdateRoomProfile") {
      const t = e;
      if (t.updates) for (const [o, n] of Object.entries(t.updates))
        this.room.profile[o] = n;
      if (t.deletes) for (const o of t.deletes)
        delete this.room.profile[o];
      const s = { updates: t.updates, deletes: t.deletes };
      for (const [o, n] of this.roomProfileUpdatedMethods)
        n(s, this);
      this.dispatchCustomEvent("roomProfileUpdated", s);
    } else if (e.type === "PeerEntered") {
      const t = e;
      this.peers.set(t.peer.id, t.peer);
      for (const [s, o] of this.peerEnteredMethods)
        o(t, this);
      this.dispatchCustomEvent("peerEntered", t);
    } else if (e.type === "PeerLeaved") {
      const t = e;
      this.peers.delete(e.peerId);
      for (const [s, o] of this.peerLeavedMethods)
        o(t, this);
      this.dispatchCustomEvent("peerLeaved", t);
    } else if (e.type === "UpdatePeerProfile") {
      const t = this.peers.get(e.sender);
      if (e.sender && t) {
        if (e.updates) for (const [o, n] of Object.entries(e.updates))
          t.profile[o] = n;
        if (e.deletes) for (const o of e.deletes)
          delete t.profile[o];
        const s = { ...e, peerId: e.sender };
        for (const [o, n] of this.peerProfileUpdatedMethods)
          n(s, this);
        this.dispatchCustomEvent("peerProfileUpdated", s);
      }
    } else if (e.type === "InvokeFunction") {
      const t = `${e.funcId}`, s = this.distributedFuncs.get(t);
      if (s === void 0) {
        console.warn("no suitable function for ", e);
        return;
      }
      const o = this.applyInvocation(s.original, e.args);
      o instanceof Promise && o.then(() => {
        var n;
        (n = s.resolve) == null || n.apply(null, arguments);
      }).catch(() => {
        var n;
        (n = s.reject) == null || n.apply(null, arguments);
      });
    } else if (e.type === "UpdateObjectState") {
      const t = this.setStateMethods.get(e.objId);
      t && t(e.state, e.objRevision);
      const s = this.shareObjects.get(e.objId);
      s && (s.revision = e.objRevision, s.update = 0);
    } else if (e.type === "InvokeMethod") {
      const t = this.shareObjects.get(e.objId);
      if (t === void 0) {
        console.error(`Object not found for id: ${e.objId}.`, e);
        return;
      }
      const s = `${e.objId}:${e.methodId}`, o = this.shareOrNotifyMethods.get(s);
      if (o === void 0) {
        console.error(`Method not found for id: ${s}.`, e);
        return;
      }
      o.config.distributed && (o.config.distributed.serialized && t.revision + 1 !== e.serverObjRevision && console.error(`Found inconsistency. serverObjRevision must be ${t.revision + 1} but ${e.serverObjRevision}.`, e), t.revision++, t.update++);
      const n = this.applyInvocation(o.original, e.args);
      n instanceof Promise && n.then(function() {
        var r;
        (r = o.resolve) == null || r.apply(null, arguments);
      }).catch(function() {
        var r;
        (r = o.reject) == null || r.apply(null, arguments);
      });
    } else if (e.type) {
      const t = e;
      for (const s of this.userMessageArrivedMethods)
        s.config.type === e.type && s.method(t, this);
      this.dispatchEvent(new CustomEvent(e.type, { detail: e }));
    } else
      console.warn("Unknown message type.", e);
  }
  isSystemMessageType(e) {
    return e in this.systemMessageTypes;
  }
  send(e, t, s = "BROADCAST") {
    this.ws && this.sendMessage({
      type: e,
      sender: this.selfPeer.id,
      castType: s,
      recipients: void 0,
      content: t
    });
  }
  unicast(e, t, s) {
    this.sendMessage({
      type: e,
      sender: this.selfPeer.id,
      castType: "UNICAST",
      recipients: [s],
      content: t
    });
  }
  multicast(e, t, s) {
    this.sendMessage({
      type: e,
      sender: this.selfPeer.id,
      castType: "MULTICAST",
      recipients: s,
      content: t
    });
  }
  broadcast(e, t) {
    this.sendMessage({
      type: e,
      sender: this.selfPeer.id,
      castType: "BROADCAST",
      recipients: void 0,
      content: t
    });
  }
  othercast(e, t) {
    this.sendMessage({
      type: e,
      sender: this.selfPeer.id,
      castType: "OTHERCAST",
      recipients: void 0,
      content: t
    });
  }
  sendMessage(e) {
    if (this.isSystemMessageType(e.type))
      throw new Error("システムメッセージは送信できません。");
    this.doSendMessage(e);
  }
  addReceiver(e, t) {
    if (this.isSystemMessageType(e))
      throw new Error("システムメッセージのレシーバは登録できません。");
    this.addEventListener(e, t);
  }
  removeReceiver(e, t) {
    this.removeEventListener(e, t);
  }
  replacer(e, t) {
    return t instanceof Map ? Object.fromEntries(t) : t;
  }
  doSendMessage(e) {
    var t;
    this.connecting ? (t = this.ws) == null || t.send(JSON.stringify(e, this.replacer)) : this.interimQueue.push(e);
  }
  registerFunction(e, t = { distributed: b }) {
    if (t.hostOnly)
      return this.addHostOnlyFunction(e, t);
    if (t.distributed || t.changeState) {
      const s = e.name, o = this.distributedFuncs.size, n = this.createFunctionProxy(e, t, o), r = function() {
        return n.apply(null, arguments);
      };
      return this.doSendMessage(A({
        definition: { funcId: o, name: s, config: t }
      })), r;
    }
    return e;
  }
  register(e, t = []) {
    if (!this.ws) return e;
    const s = e;
    if (s.madoiObjectId_)
      return console.warn("Ignore object registration because it's already registered."), e;
    let o = s.constructor.name;
    s.__proto__.constructor.madoiClassConfig_ && (o = s.__proto__.constructor.madoiClassConfig_.className);
    const n = this.shareObjects.size, r = { instance: s, revision: 0, update: 0 };
    this.shareObjects.set(n, r), s.madoiObjectId_ = n;
    const f = new Array(), u = new Array(), M = /* @__PURE__ */ new Map();
    Object.getOwnPropertyNames(Object.getPrototypeOf(s)).forEach((h) => {
      const c = s[h];
      if (typeof c != "function" || !c.madoiMethodConfig_) return;
      const p = c.madoiMethodConfig_, d = f.length;
      M.set(h, d), f.push(c), u.push({ methodId: d, name: h, config: p }), console.debug(`add config ${o}.${h}=${JSON.stringify(p)} from decorator`);
    });
    for (const h of t) {
      const c = h.method, p = h, d = c.name, y = M.get(d);
      if (typeof y > "u") {
        p.distributed && (p.distributed = { ...b, ...p.distributed }), p.getState && (p.getState = { ...R, ...p.distributed });
        const g = f.length;
        M.set(d, g), f.push(c), u.push({ methodId: g, name: h.method.name, config: p }), console.debug(`add config ${o}.${d}=${JSON.stringify(h)} from argument`);
      } else
        u[y].config = {
          ...u[y].config,
          ...h
        }, console.debug(`merge config ${o}.${d}=${JSON.stringify(h)} from argument`);
    }
    for (let h = 0; h < f.length; h++) {
      const c = f[h], p = u[h], d = p.config;
      d.distributed || d.changeState ? s[p.name] = this.createMethodProxy(
        c.bind(s),
        d,
        n,
        p.methodId
      ) : d.hostOnly ? s[p.name] = this.addHostOnlyFunction(
        c.bind(s),
        p.config,
        n
      ) : d.getState ? this.getStateMethods.set(n, {
        method: c.bind(s),
        config: d.getState,
        firstObjModified: -1,
        lastObjModified: -1
      }) : d.setState ? this.setStateMethods.set(n, c.bind(s)) : d.beforeEnterRoom ? this.beforeEnterRoomMethods.set(n, c.bind(s)) : d.enterRoomAllowed ? this.enterRoomAllowedMethods.set(n, c.bind(s)) : d.enterRoomDenied ? this.enterRoomDeniedMethods.set(n, c.bind(s)) : d.leaveRoomDone ? this.leaveRoomDoneMethods.set(n, c.bind(s)) : d.peerEntered ? this.peerEnteredMethods.set(n, c.bind(s)) : d.peerProfileUpdated ? this.peerProfileUpdatedMethods.set(n, c.bind(s)) : d.peerLeaved ? this.peerLeavedMethods.set(n, c.bind(s)) : d.userMessageArrived && this.userMessageArrivedMethods.push({
        method: c.bind(s),
        config: d.userMessageArrived
      });
    }
    return this.doSendMessage(D({
      definition: { objId: n, className: o, methods: u }
    })), e;
  }
  createFunctionProxy(e, t, s) {
    const o = `${s}`, n = { original: e, config: t };
    this.distributedFuncs.set(o, n), n.promise = new Promise((f, u) => {
      n.resolve = f, n.reject = u;
    });
    const r = this;
    return function() {
      if (r.ws === null) {
        if (e) return e.apply(null, arguments);
      } else {
        let f = null, u = "BROADCAST";
        return t.distributed && !t.distributed.serialized && (f = e.apply(null, arguments), u = "OTHERCAST"), r.sendMessage(T(
          u,
          { funcId: s, args: Array.from(arguments) }
        )), f ?? n.promise;
      }
    };
  }
  createMethodProxy(e, t, s, o) {
    const n = `${s}:${o}`, r = { original: e, config: t };
    this.shareOrNotifyMethods.set(n, r), r.promise = new Promise((u, M) => {
      r.resolve = u, r.reject = M;
    });
    const f = this;
    return function() {
      if (f.ws === null) {
        if (e) return e.apply(null, [...arguments, f]);
      } else {
        let u = null, M = "BROADCAST";
        const h = f.shareObjects.get(s), c = h.revision;
        return t.distributed && !t.distributed.serialized && (u = e.apply(null, [...arguments, f]), M = "OTHERCAST"), t.changeState && (h.revision++, h.update++), f.sendMessage(U(
          M,
          {
            objId: s,
            objRevision: c,
            methodId: o,
            args: Array.from(arguments)
          }
        )), u ?? r.promise;
      }
    };
  }
  addHostOnlyFunction(e, t, s) {
    const o = this;
    return function() {
      o.isSelfPeerHost() && (t.changeState && s !== void 0 && o.objectChanged(s), e.apply(null, [...arguments, o]));
    };
  }
  objectChanged(e) {
    const t = this.shareObjects.get(e);
    t.revision++, t.update++;
    const s = this.getStateMethods.get(e);
    if (!s) return;
    const o = performance.now();
    s.firstObjModified == -1 && (s.firstObjModified = o), s.lastObjModified = o;
  }
  saveStates() {
    if (!(!this.ws || !this.connecting) && this.isSelfPeerHost())
      for (let [e, t] of this.shareObjects) {
        if (t.update == 0) continue;
        const s = this.getStateMethods.get(e);
        if (!s) continue;
        const o = s.config, n = performance.now(), r = s.firstObjModified, f = s.lastObjModified;
        (n - f >= (o.minInterval || 0) || n - r >= (o.maxInterval || 0)) && (this.doSendMessage($({
          objId: e,
          objRevision: t.revision,
          state: s.method(this)
        })), s.firstObjModified = -1, s.lastObjModified = -1, t.update = 0, console.debug(`state saved: ${e}`));
      }
  }
  applyInvocation(e, t) {
    return e.apply(null, t);
  }
  isSelfPeerHost() {
    for (const e of this.peers.values())
      if (e.order < this.selfPeer.order) return !1;
    return !0;
  }
}
export {
  B as BeforeEnterRoom,
  x as ChangeState,
  F as ClassName,
  N as Distributed,
  Q as EnterRoomAllowed,
  q as EnterRoomDenied,
  H as GetState,
  z as HostOnly,
  G as LeaveRoomDone,
  Z as Madoi,
  W as PeerEntered,
  K as PeerLeaved,
  X as PeerProfileUpdated,
  V as RoomProfileUpdated,
  J as SetState,
  Y as UserMessageArrived,
  A as newDefineFunction,
  D as newDefineObject,
  I as newEnterRoom,
  T as newInvokeFunction,
  U as newInvokeMethod,
  L as newLeaveRoom,
  _ as newPing,
  $ as newUpdateObjectState,
  S as newUpdatePeerProfile,
  P as newUpdateRoomProfile
};
