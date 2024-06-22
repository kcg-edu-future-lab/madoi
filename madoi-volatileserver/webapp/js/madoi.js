!function(e,t){"object"==typeof exports&&"object"==typeof module?module.exports=t():"function"==typeof define&&define.amd?define([],t):"object"==typeof exports?exports.madoi=t():e.madoi=t()}(self,(()=>(()=>{"use strict";var e={d:(t,o)=>{for(var s in o)e.o(o,s)&&!e.o(t,s)&&Object.defineProperty(t,s,{enumerable:!0,get:o[s]})},o:(e,t)=>Object.prototype.hasOwnProperty.call(e,t),r:e=>{"undefined"!=typeof Symbol&&Symbol.toStringTag&&Object.defineProperty(e,Symbol.toStringTag,{value:"Module"}),Object.defineProperty(e,"__esModule",{value:!0})}},t={};e.r(t),e.d(t,{EnterRoomAllowed:()=>w,EnterRoomDenied:()=>R,GetState:()=>P,HostOnly:()=>b,LeaveRoomDone:()=>O,Madoi:()=>C,PeerEntered:()=>j,PeerLeaved:()=>E,PeerProfileUpdated:()=>_,RoomProfileUpdated:()=>I,SetState:()=>S,Share:()=>M,ShareClass:()=>g,getStateConfigDefault:()=>v,newDefineFunction:()=>l,newDefineObject:()=>c,newEnterRoom:()=>i,newInvokeFunction:()=>h,newInvokeMethod:()=>f,newLeaveRoom:()=>r,newPing:()=>n,newUpdateObjectState:()=>p,newUpdatePeerProfile:()=>a,newUpdateRoomProfile:()=>d,shareConfigDefault:()=>y});const o={sender:"__PEER__",castType:"PEERTOSERVER",recipients:void 0},s={sender:"__PEER__",castType:"BROADCAST",recipients:void 0};function n(e=void 0){return{type:"Ping",...o,body:e}}function i(e){return{type:"EnterRoom",...o,...e}}function r(e){return{type:"LeaveRoom",...o,...e}}function d(e){return{type:"UpdateRoomProfile",...s,...e}}function a(e){return{type:"UpdatePeerProfile",...s,...e}}function c(e){return{type:"DefineObject",...o,...e}}function l(e){return{type:"DefineFunction",...o,...e}}function f(e,t){return{type:"InvokeMethod",...s,...t,castType:e}}function h(e,t){return{type:"InvokeFunction",...s,castType:e,...t}}function p(e){return{type:"UpdateObjectState",...s,...e}}class u extends EventTarget{}class m extends u{fire(e,t){super.dispatchEvent(new CustomEvent(e,{detail:t}))}}function g(e={}){return t=>{t.madoiClassConfig_=e}}const y={type:"beforeExec",maxLog:0,allowedTo:["USER"]};function M(e=y){const t=e;return t.type||(t.type="beforeExec"),t.maxLog||(t.maxLog=0),(e,o,s)=>{const n={share:t};e[o].madoiMethodConfig_=n}}const v={maxInterval:5e3};function P(e=v){const t=e;return(e,o,s)=>{const n={getState:t};e[o].madoiMethodConfig_=n}}function S(e={}){const t=e;return(e,o,s)=>{const n={setState:t};e[o].madoiMethodConfig_=n}}function b(e={}){return(t,o,s)=>{const n={hostOnly:e};t[o].madoiMethodConfig_=n}}function w(e={}){const t=e;return(e,o,s)=>{const n={enterRoomAllowed:t};e[o].madoiMethodConfig_=n}}function R(e={}){const t=e;return(e,o,s)=>{const n={enterRoomDenied:t};e[o].madoiMethodConfig_=n}}function O(e={}){const t=e;return(e,o,s)=>{const n={leaveRoomDone:t};e[o].madoiMethodConfig_=n}}function I(e={}){const t=e;return(e,o,s)=>{const n={roomProfileUpdated:t};e[o].madoiMethodConfig_=n}}function j(e={}){const t=e;return(e,o,s)=>{const n={peerEntered:t};e[o].madoiMethodConfig_=n}}function E(e={}){const t=e;return(e,o,s)=>{const n={peerLeaved:t};e[o].madoiMethodConfig_=n}}function _(e={}){const t=e;return(e,o,s)=>{const n={peerProfileUpdated:t};e[o].madoiMethodConfig_=n}}class C extends m{connecting=!1;interimQueue;sharedFunctions=[];sharedObjects=[];getStateMethods=new Map;setStateMethods=new Map;enterRoomAllowedMethods=new Map;enterRoomDeniedMethods=new Map;leaveRoomDoneMethods=new Map;roomProfileUpdatedMethods=new Map;peerEnteredMethods=new Map;peerLeavedMethods=new Map;peerProfileUpdatedMethods=new Map;promises={};objectModifications=new Map;objectRevisions=new Map;url;ws=null;room={id:"",profile:{}};selfPeer;peers=new Map;currentSender=null;constructor(e,t,o){if(super(),this.selfPeer=t?{...t,order:-1}:{id:"",order:-1,profile:{}},this.interimQueue=new Array,this.doSendMessage(i({roomProfile:o,selfPeer:{id:"",profile:{},...t,order:-1}})),e.match(/^wss?:\/\//))this.url=`${e}`;else{const t=document.querySelector("script[src$='madoi.js']").src.split("/",5),o=("http:"==t[0]?"ws:":"wss:")+"//"+t[2]+"/"+t[3];this.url=`${o}/rooms/${e}`}this.ws=new WebSocket(this.url),this.ws.onopen=e=>this.handleOnOpen(e),this.ws.onclose=e=>this.handleOnClose(e),this.ws.onerror=e=>this.handleOnError(e),this.ws.onmessage=e=>this.handleOnMessage(e),setInterval((()=>{this.saveStates()}),1e3),setInterval((()=>{this.sendPing()}),3e4)}getRoomProfile(){return this.room?.profile}setRoomProfile(e,t){const o={};o[e]=t,this.sendMessage(d({updates:o}))}removeRoomProfile(e){this.sendMessage(d({deletes:[e]}))}getSelfPeerId(){return this.selfPeer?.id}getSelfPeerProfile(){return this.selfPeer.profile}setSelfPeerProfile(e,t){this.selfPeer.profile[e]=t;const o={};o[e]=t,this.sendMessage(a({updates:o}))}removeSelfPeerProfile(e){delete this.selfPeer.profile[e],this.sendMessage(a({deletes:[e]}))}getCurrentSender(){return this.currentSender?this.peers.get(this.currentSender):null}isCurrentSenderSelf(){return this.currentSender===this.selfPeer.id}close(){this.ws?.close()}sendPing(){this.ws?.send(JSON.stringify(n()))}handleOnOpen(e){this.connecting=!0;for(let e of this.interimQueue)this.ws?.send(JSON.stringify(e));this.interimQueue=[]}handleOnClose(e){console.debug(`websocket closed because: ${e.reason}.`),this.connecting=!1,this.ws=null}handleOnError(e){}handleOnMessage(e){const t=JSON.parse(e.data);this.currentSender=t.sender,this.data(t)}data(e){if("Pong"==e.type);else if("EnterRoomAllowed"===e.type){const t=e;for(const[e,o]of this.enterRoomAllowedMethods)o(t);this.room=e.room,this.selfPeer.order=e.selfPeer.order,this.peers.set(t.selfPeer.id,{...t.selfPeer,profile:this.selfPeer.profile});for(const e of t.otherPeers)this.peers.set(e.id,e);if(this.fire("enterRoomAllowed",t),e.histories)for(const t of e.histories)this.data(t)}else if("EnterRoomDenied"===e.type){const t=e;for(const[e,o]of this.enterRoomDeniedMethods)o(t);this.fire("enterRoomDenied",t)}else if("LeaveRoomDone"==e.type){for(const[e,t]of this.leaveRoomDoneMethods)t();this.fire("leaveRoomDone",{})}else if("UpdateRoomProfile"===e.type){const t=e;if(e.updates)for(const[t,o]of Object.entries(e.updates))this.room.profile[t]=o;if(e.deletes)for(const t of e.deletes)delete this.room.profile[t];for(const[e,o]of this.roomProfileUpdatedMethods)o(t);this.fire("roomProfileUpdated",t)}else if("PeerEntered"===e.type){const t=e;this.peers.set(t.peer.id,t.peer);for(const[e,o]of this.peerEnteredMethods)o(t);this.fire("peerEntered",{peer:t.peer})}else if("PeerLeaved"===e.type){this.peers.delete(e.peerId);for(const[t,o]of this.peerLeavedMethods)o(e.peerId);this.fire("peerLeaved",e.peerId)}else if("UpdatePeerProfile"===e.type){const t=this.peers.get(e.sender);if(e.sender&&t){if(e.updates)for(const[o,s]of Object.entries(e.updates))t.profile[o]=s;if(e.deletes)for(const o of e.deletes)delete t.profile[o];const o={...e,peerId:e.peerId};for(const[e,t]of this.peerProfileUpdatedMethods)t(o);this.fire("peerProfileUpdated",o)}}else if("InvokeMethod"===e.type){if(e.objId){const t=this.objectRevisions.get(e.objId);this.objectRevisions.set(e.objId,(t||0)+1)}const t=this.sharedFunctions[e.methodId];if(t){const o=this.applyInvocation(t,e.args);o instanceof Promise&&o.then((()=>{this.promises[e.methodId].resolve.apply(null,arguments)})).catch((()=>{this.promises[e.methodId].reject.apply(null,arguments)}))}else console.warn("no suitable method for ",e)}else if("InvokeFunction"===e.type){const t=this.sharedFunctions[e.funcId];if(t){const o=this.applyInvocation(t,e.args);o instanceof Promise&&o.then((()=>{this.promises[e.methodId].resolve.apply(null,arguments)})).catch((()=>{this.promises[e.methodId].reject.apply(null,arguments)}))}else console.warn("no suitable function for ",e)}else if("UpdateObjectState"===e.type){const t=this.setStateMethods.get(e.objId);t&&t(JSON.parse(e.state),e.revision),this.objectRevisions.set(e.objId,e.revision)}else e.type?this.dispatchEvent(new CustomEvent(e.type,{detail:e})):console.log("Unknown message type.",e)}systemMessageTypes=["Ping","Pong","EnterRoom","EnterRoomAllowed","EnterRoomDenied","LeaveRoom","LeaveRoomDone","UpdateRoomProfile","PeerArrived","PeerLeaved","UpdatePeerProfile","DefineFunction","DefineObject","InvokeFunction","UpdateObjectState","InvokeMethod"];isSystemMessageType(e){return e in this.systemMessageTypes}send(e,t,o="BROADCAST"){this.ws&&this.sendMessage({type:e,sender:this.selfPeer.id,castType:o,recipients:void 0,content:t})}unicast(e,t,o){this.sendMessage({type:e,sender:this.selfPeer.id,castType:"UNICAST",recipients:[o],content:t})}multicast(e,t,o){this.sendMessage({type:e,sender:this.selfPeer.id,castType:"MULTICAST",recipients:o,content:t})}broadcast(e,t){this.sendMessage({type:e,sender:this.selfPeer.id,castType:"BROADCAST",recipients:void 0,content:t})}othercast(e,t){this.sendMessage({type:e,sender:this.selfPeer.id,castType:"OTHERCAST",recipients:void 0,content:t})}sendMessage(e){if(this.isSystemMessageType(e.type))throw new Error("システムメッセージは送信できません。");this.doSendMessage(e)}addReceiver(e,t){if(this.isSystemMessageType(e))throw new Error("システムメッセージのレシーバは登録できません。");this.addEventListener(e,t)}removeReceiver(e,t){this.removeEventListener(e,t)}doSendMessage(e){this.connecting?this.ws?.send(JSON.stringify(e)):this.interimQueue.push(e)}register(e,t=[]){if(!this.ws)return e;const o=e;if(o.madoiObjectId_)return console.warn("Ignore object registration because it's already registered."),e;let s=o.constructor.name;o.__proto__.constructor.madoiClassConfig_&&(s=o.__proto__.constructor.madoiClassConfig_.className);const n=this.sharedObjects.length;this.sharedObjects.push(o),o.madoiObjectId_=n;const i=new Array,r=new Array,d=new Map;Object.getOwnPropertyNames(o.__proto__).forEach((e=>{const t=o[e];if("function"!=typeof t)return;if(!t.madoiMethodConfig_)return;const n=t.madoiMethodConfig_,a=i.length;d.set(e,a),i.push(t),r.push({methodId:a,name:e,config:n}),console.debug(`add config ${s}.${e}=${JSON.stringify(n)} from decorator`)}));for(const e of t){const t=e.method,o=e,n=t.name;if("share"in o)o.share.type||(o.share.type=y.type),o.share.maxLog||(o.share.maxLog=y.maxLog);else if("hostOnly"in o);else if("getState"in o)o.getState.maxInterval||(o.getState.maxInterval=v.maxInterval);else if("setState"in o);else if("enterRoomAllowed"in o);else if("enterRoomDenied"in o);else if("leaveRoomDone"in o);else if("peerEntered"in o);else if(!("peerLeaved"in o))continue;const a=d.get(n);if(void 0===a){const a=i.length;d.set(n,a),i.push(t),r.push({methodId:a,name:e.method.name,config:o}),console.debug(`add config ${s}.${n}=${JSON.stringify(e)} from argument`)}else r[a].config=e,console.debug(`replace config ${s}.${n}=${JSON.stringify(e)} from argument`)}for(let e=0;e<i.length;e++){const t=i[e],s=r[e],d=s.config;if("share"in d){const[e,i]=this.addSharedFunction(t.bind(o),d.share,n);s.methodId=e,this.objectModifications.set(n,0),this.objectRevisions.set(n,0);const r=this;o[t.name]=function(){const e=r.objectModifications.get(n);r.objectModifications.set(n,e+1);const t=r.objectRevisions.get(n);return r.objectRevisions.set(n,t+1),i.apply(null,arguments)}}else if("hostOnly"in d){const e=this.addHostOnlyFunction(t.bind(o),d.hostOnly);o[t.name]=function(){return e.apply(null,arguments)}}else"getState"in d?this.getStateMethods.set(n,{method:t.bind(o),config:d.getState,lastGet:0}):"setState"in d?this.setStateMethods.set(n,t.bind(o)):"enterRoomAllowed"in d?this.enterRoomAllowedMethods.set(n,t.bind(o)):"enterRoomDenied"in d?this.enterRoomDeniedMethods.set(n,t.bind(o)):"leaveRoomDone"in d?this.leaveRoomDoneMethods.set(n,t.bind(o)):"peerEntered"in d?this.peerEnteredMethods.set(n,t.bind(o)):"peerProfileUpdated"in d?this.peerProfileUpdatedMethods.set(n,t.bind(o)):"peerLeaved"in d&&this.peerLeavedMethods.set(n,t.bind(o))}const a=c({definition:{objId:n,className:s,methods:r}});return this.doSendMessage(a),e}registerFunction(e,t={share:{}}){if("hostOnly"in t)return this.addHostOnlyFunction(e,t);if("share"in t){t.share.type||(t.share.type=y.type),t.share.maxLog||(t.share.maxLog=y.maxLog);const o=e.name,[s,n]=this.addSharedFunction(e,t.share),i=function(){return n.apply(null,arguments)};return this.doSendMessage(l({definition:{funcId:s,name:o,config:t}})),i}return e}addSharedFunction(e,t,o){const s=this.sharedFunctions.length;this.sharedFunctions.push(e),this.promises[s]={},this.promises[s].promise=new Promise(((e,t)=>{this.promises[s].resolve=e,this.promises[s].reject=t}));const n=this;return[s,function(){if(null!==n.ws){let i=null,r="BROADCAST";return"afterExec"===t.type&&(i=e.apply(null,arguments),r="OTHERCAST"),void 0!==o?n.sendMessage(f(r,{objId:o,methodId:s,args:Array.from(arguments)})):n.sendMessage(h(r,{funcId:s,args:Array.from(arguments)})),null!=i?i:n.promises[s].promise}if(e)return e.apply(null,arguments)}]}addHostOnlyFunction(e,t){const o=this;return function(){let t=o.selfPeer.order;for(const e of o.peers.values())t>e.order&&(t=e.order);o.selfPeer.order===t&&e.apply(null,arguments)}}saveStates(){if(this.ws&&this.connecting)for(let[e,t]of this.objectModifications){if(0==t)continue;const o=this.getStateMethods.get(e);if(!o)continue;const s=performance.now();(o.config.maxUpdates&&o.config.maxUpdates<=t||o.config.maxInterval&&o.config.maxInterval<=s-o.lastGet)&&(this.doSendMessage(p({objId:e,state:JSON.stringify(o.method()),revision:this.objectRevisions.get(e)})),o.lastGet=s,this.objectModifications.set(e,0),console.log(`state saved: ${e}`))}}applyInvocation(e,t){return e.apply(null,t)}}return t})()));