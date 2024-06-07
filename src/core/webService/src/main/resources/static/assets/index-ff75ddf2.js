var Oe=Object.defineProperty;var Se=(t,e,r)=>e in t?Oe(t,e,{enumerable:!0,configurable:!0,writable:!0,value:r}):t[e]=r;var Y=(t,e,r)=>(Se(t,typeof e!="symbol"?e+"":e,r),r);(function(){const e=document.createElement("link").relList;if(e&&e.supports&&e.supports("modulepreload"))return;for(const o of document.querySelectorAll('link[rel="modulepreload"]'))n(o);new MutationObserver(o=>{for(const l of o)if(l.type==="childList")for(const i of l.addedNodes)i.tagName==="LINK"&&i.rel==="modulepreload"&&n(i)}).observe(document,{childList:!0,subtree:!0});function r(o){const l={};return o.integrity&&(l.integrity=o.integrity),o.referrerPolicy&&(l.referrerPolicy=o.referrerPolicy),o.crossOrigin==="use-credentials"?l.credentials="include":o.crossOrigin==="anonymous"?l.credentials="omit":l.credentials="same-origin",l}function n(o){if(o.ep)return;o.ep=!0;const l=r(o);fetch(o.href,l)}})();function y(){}function ge(t){return t()}function ce(){return Object.create(null)}function q(t){t.forEach(ge)}function ye(t){return typeof t=="function"}function D(t,e){return t!=t?e==e:t!==e||t&&typeof t=="object"||typeof t=="function"}function Pe(t){return Object.keys(t).length===0}function h(t,e){t.appendChild(e)}function g(t,e,r){t.insertBefore(e,r||null)}function $(t){t.parentNode&&t.parentNode.removeChild(t)}function ke(t,e){for(let r=0;r<t.length;r+=1)t[r]&&t[r].d(e)}function p(t){return document.createElement(t)}function E(t){return document.createTextNode(t)}function v(){return E(" ")}function V(){return E("")}function te(t,e,r,n){return t.addEventListener(e,r,n),()=>t.removeEventListener(e,r,n)}function ne(t){return function(e){return e.preventDefault(),t.call(this,e)}}function k(t,e,r){r==null?t.removeAttribute(e):t.getAttribute(e)!==r&&t.setAttribute(e,r)}function Ne(t){return Array.from(t.childNodes)}function z(t,e){e=""+e,t.data!==e&&(t.data=e)}function Ae(t,e,{bubbles:r=!1,cancelable:n=!1}={}){return new CustomEvent(t,{detail:e,bubbles:r,cancelable:n})}let F;function H(t){F=t}function ve(){if(!F)throw new Error("Function called outside component initialization");return F}function re(t){ve().$$.on_mount.push(t)}function oe(){const t=ve();return(e,r,{cancelable:n=!1}={})=>{const o=t.$$.callbacks[e];if(o){const l=Ae(e,r,{cancelable:n});return o.slice().forEach(i=>{i.call(t,l)}),!l.defaultPrevented}return!0}}function fe(t,e){const r=t.$$.callbacks[e.type];r&&r.slice().forEach(n=>n.call(this,e))}const j=[],W=[];let I=[];const Q=[],De=Promise.resolve();let X=!1;function Te(){X||(X=!0,De.then(we))}function Z(t){I.push(t)}function x(t){Q.push(t)}const J=new Set;let U=0;function we(){if(U!==0)return;const t=F;do{try{for(;U<j.length;){const e=j[U];U++,H(e),Be(e.$$)}}catch(e){throw j.length=0,U=0,e}for(H(null),j.length=0,U=0;W.length;)W.pop()();for(let e=0;e<I.length;e+=1){const r=I[e];J.has(r)||(J.add(r),r())}I.length=0}while(j.length);for(;Q.length;)Q.pop()();X=!1,J.clear(),H(t)}function Be(t){if(t.fragment!==null){t.update(),q(t.before_update);const e=t.dirty;t.dirty=[-1],t.fragment&&t.fragment.p(t.ctx,e),t.after_update.forEach(Z)}}function Me(t){const e=[],r=[];I.forEach(n=>t.indexOf(n)===-1?e.push(n):r.push(n)),r.forEach(n=>n()),I=e}const G=new Set;let N;function Ce(){N={r:0,c:[],p:N}}function Le(){N.r||q(N.c),N=N.p}function C(t,e){t&&t.i&&(G.delete(t),t.i(e))}function L(t,e,r,n){if(t&&t.o){if(G.has(t))return;G.add(t),N.c.push(()=>{G.delete(t),n&&(r&&t.d(1),n())}),t.o(e)}else n&&n()}function K(t){return(t==null?void 0:t.length)!==void 0?t:Array.from(t)}function ee(t,e,r){const n=t.$$.props[e];n!==void 0&&(t.$$.bound[n]=r,r(t.$$.ctx[n]))}function A(t){t&&t.c()}function O(t,e,r){const{fragment:n,after_update:o}=t.$$;n&&n.m(e,r),Z(()=>{const l=t.$$.on_mount.map(ge).filter(ye);t.$$.on_destroy?t.$$.on_destroy.push(...l):q(l),t.$$.on_mount=[]}),o.forEach(Z)}function S(t,e){const r=t.$$;r.fragment!==null&&(Me(r.after_update),q(r.on_destroy),r.fragment&&r.fragment.d(e),r.on_destroy=r.fragment=null,r.ctx=[])}function Ue(t,e){t.$$.dirty[0]===-1&&(j.push(t),Te(),t.$$.dirty.fill(0)),t.$$.dirty[e/31|0]|=1<<e%31}function T(t,e,r,n,o,l,i=null,s=[-1]){const f=F;H(t);const u=t.$$={fragment:null,ctx:[],props:l,update:y,not_equal:o,bound:ce(),on_mount:[],on_destroy:[],on_disconnect:[],before_update:[],after_update:[],context:new Map(e.context||(f?f.$$.context:[])),callbacks:ce(),dirty:s,skip_bound:!1,root:e.target||f.$$.root};i&&i(u.root);let a=!1;if(u.ctx=r?r(t,e.props||{},(c,d,..._)=>{const m=_.length?_[0]:d;return u.ctx&&o(u.ctx[c],u.ctx[c]=m)&&(!u.skip_bound&&u.bound[c]&&u.bound[c](m),a&&Ue(t,c)),d}):[],u.update(),a=!0,q(u.before_update),u.fragment=n?n(u.ctx):!1,e.target){if(e.hydrate){const c=Ne(e.target);u.fragment&&u.fragment.l(c),c.forEach($)}else u.fragment&&u.fragment.c();e.intro&&C(t.$$.fragment),O(t,e.target,e.anchor),we()}H(f)}class B{constructor(){Y(this,"$$");Y(this,"$$set")}$destroy(){S(this,1),this.$destroy=y}$on(e,r){if(!ye(r))return y;const n=this.$$.callbacks[e]||(this.$$.callbacks[e]=[]);return n.push(r),()=>{const o=n.indexOf(r);o!==-1&&n.splice(o,1)}}$set(e){this.$$set&&!Pe(e)&&(this.$$.skip_bound=!0,this.$$set(e),this.$$.skip_bound=!1)}}const je="4";typeof window<"u"&&(window.__svelte||(window.__svelte={v:new Set})).v.add(je);function ae(t,e,r){const n=t.slice();return n[2]=e[r],n}function de(t){let e,r,n=t[2]+"",o,l,i,s=t[0][t[2]]+"",f,u;return{c(){e=p("tr"),r=p("td"),o=E(n),l=v(),i=p("td"),f=E(s),u=v()},m(a,c){g(a,e,c),h(e,r),h(r,o),h(e,l),h(e,i),h(i,f),h(e,u)},p(a,c){c&1&&n!==(n=a[2]+"")&&z(o,n),c&1&&s!==(s=a[0][a[2]]+"")&&z(f,s)},d(a){a&&$(e)}}}function Ie(t){let e,r,n,o,l=K(Object.keys(t[0])),i=[];for(let s=0;s<l.length;s+=1)i[s]=de(ae(t,l,s));return{c(){e=p("table"),r=p("thead"),r.innerHTML="<tr><th>Asset</th> <th>Balance</th></tr>",n=v(),o=p("tbody");for(let s=0;s<i.length;s+=1)i[s].c()},m(s,f){g(s,e,f),h(e,r),h(e,n),h(e,o);for(let u=0;u<i.length;u+=1)i[u]&&i[u].m(o,null)},p(s,[f]){if(f&1){l=K(Object.keys(s[0]));let u;for(u=0;u<l.length;u+=1){const a=ae(s,l,u);i[u]?i[u].p(a,f):(i[u]=de(a),i[u].c(),i[u].m(o,null))}for(;u<i.length;u+=1)i[u].d(1);i.length=l.length}},i:y,o:y,d(s){s&&$(e),ke(i,s)}}}function Re(t,e,r){let n={};function o(){fetch("balances/list").then(l=>l.json()).then(l=>r(0,n=l))}return re(()=>{o()}),[n,o]}class He extends B{constructor(e){super(),T(this,e,Re,Ie,D,{refresh:1})}get refresh(){return this.$$.ctx[1]}}function he(t){let e,r=t[0].name+"",n,o,l;return{c(){e=E("Hello, "),n=E(r),o=E(". "),l=p("a"),l.textContent="Logout",k(l,"href","/logout")},m(i,s){g(i,e,s),g(i,n,s),g(i,o,s),g(i,l,s)},p(i,s){s&1&&r!==(r=i[0].name+"")&&z(n,r)},d(i){i&&($(e),$(n),$(o),$(l))}}}function ze(t){let e,r=t[0]!=null&&he(t);return{c(){r&&r.c(),e=V()},m(n,o){r&&r.m(n,o),g(n,e,o)},p(n,[o]){n[0]!=null?r?r.p(n,o):(r=he(n),r.c(),r.m(e.parentNode,e)):r&&(r.d(1),r=null)},i:y,o:y,d(n){n&&$(e),r&&r.d(n)}}}async function Fe(){try{const t=await fetch("system/whoami");if(!t.ok)throw new Error(`Error! status: ${t.status}`);return await t.json()}catch(t){return t instanceof Error?(console.log("error message: ",t.message),null):(console.log("unexpected error: ",t),null)}}function We(t,e,r){let{user:n=null}=e;return re(async()=>{r(0,n=await Fe())}),t.$$set=o=>{"user"in o&&r(0,n=o.user)},[n]}class qe extends B{constructor(e){super(),T(this,e,We,ze,D,{user:0})}}function Ge(t){let e,r,n;return{c(){e=p("form"),e.innerHTML='<label>Asset<input type="text" name="assetCode" placeholder="USD"/></label> <label>Amount<input type="text" name="amount" placeholder="100"/></label> <input type="submit" value="Deposit"/>',k(e,"method","post"),k(e,"action","/balances/deposit")},m(o,l){g(o,e,l),r||(n=te(e,"submit",ne(t[0])),r=!0)},p:y,i:y,o:y,d(o){o&&$(e),r=!1,n()}}}function Ke(t){const e=oe();return[n=>{const o=n.target.action,l=new FormData(n.target),i=new URLSearchParams;for(let s of l){const[f,u]=s;i.append(f,u)}fetch(o,{method:"POST",body:i}).then(s=>{n.target.reset(),e("deposit",{})})}]}class Ve extends B{constructor(e){super(),T(this,e,Ke,Ge,D,{})}}function me(t,e,r){const n=t.slice();return n[4]=e[r],n}function Ye(t){let e,r=K(t[0]),n=[];for(let o=0;o<r.length;o+=1)n[o]=_e(me(t,r,o));return{c(){for(let o=0;o<n.length;o+=1)n[o].c();e=V()},m(o,l){for(let i=0;i<n.length;i+=1)n[i]&&n[i].m(o,l);g(o,e,l)},p(o,l){if(l&7){r=K(o[0]);let i;for(i=0;i<r.length;i+=1){const s=me(o,r,i);n[i]?n[i].p(s,l):(n[i]=_e(s),n[i].c(),n[i].m(e.parentNode,e))}for(;i<n.length;i+=1)n[i].d(1);n.length=r.length}},d(o){o&&$(e),ke(n,o)}}}function Je(t){let e;return{c(){e=p("tr"),e.innerHTML='<td colspan="3">No entries.</td>'},m(r,n){g(r,e,n)},p:y,d(r){r&&$(e)}}}function pe(t){let e,r,n,o,l,i,s,f,u;return{c(){e=p("form"),r=p("input"),o=v(),l=p("input"),i=v(),s=p("input"),k(r,"type","hidden"),k(r,"name","id"),r.value=n=t[4].id,k(l,"type","hidden"),k(l,"name","instrumentCode"),l.value=t[1],k(s,"type","submit"),s.value="Cancel",k(e,"class","order-cancel"),k(e,"method","post"),k(e,"action","/orders/cancel")},m(a,c){g(a,e,c),h(e,r),h(e,o),h(e,l),h(e,i),h(e,s),f||(u=te(e,"submit",ne(t[2])),f=!0)},p(a,c){c&1&&n!==(n=a[4].id)&&(r.value=n),c&2&&(l.value=a[1])},d(a){a&&$(e),f=!1,u()}}}function _e(t){let e,r,n=t[4].price+"",o,l,i,s=t[4].size+"",f,u,a,c,d=t[4].canCancel&&pe(t);return{c(){e=p("tr"),r=p("td"),o=E(n),l=v(),i=p("td"),f=E(s),u=v(),a=p("td"),d&&d.c(),c=v()},m(_,m){g(_,e,m),h(e,r),h(r,o),h(e,l),h(e,i),h(i,f),h(e,u),h(e,a),d&&d.m(a,null),h(e,c)},p(_,m){m&1&&n!==(n=_[4].price+"")&&z(o,n),m&1&&s!==(s=_[4].size+"")&&z(f,s),_[4].canCancel?d?d.p(_,m):(d=pe(_),d.c(),d.m(a,null)):d&&(d.d(1),d=null)},d(_){_&&$(e),d&&d.d()}}}function Qe(t){let e;function r(l,i){return l[0].length===0?Je:Ye}let n=r(t),o=n(t);return{c(){o.c(),e=V()},m(l,i){o.m(l,i),g(l,e,i)},p(l,[i]){n===(n=r(l))&&o?o.p(l,i):(o.d(1),o=n(l),o&&(o.c(),o.m(e.parentNode,e)))},i:y,o:y,d(l){l&&$(e),o.d(l)}}}function Xe(t,e,r){const n=oe();let{bookEntryList:o}=e,{instrumentCode:l}=e;const i=s=>{const f=s.target.action,u=new FormData(s.target),a=new URLSearchParams;for(let c of u){const[d,_]=c;a.append(d,_)}fetch(f,{method:"POST",body:a}).then(c=>{n("order-canceled")})};return t.$$set=s=>{"bookEntryList"in s&&r(0,o=s.bookEntryList),"instrumentCode"in s&&r(1,l=s.instrumentCode)},[o,l,i]}class be extends B{constructor(e){super(),T(this,e,Xe,Qe,D,{bookEntryList:0,instrumentCode:1})}}function $e(t){let e,r,n,o,l,i,s,f,u,a;return l=new be({props:{instrumentCode:t[0],bookEntryList:t[1].asks.reverse()}}),l.$on("order-canceled",t[3]),u=new be({props:{instrumentCode:t[0],bookEntryList:t[1].bids.reverse()}}),u.$on("order-canceled",t[4]),{c(){e=p("table"),r=p("thead"),r.innerHTML="<tr><th>Price</th> <th>Size</th> <th></th></tr>",n=v(),o=p("tbody"),A(l.$$.fragment),i=v(),s=p("tr"),s.innerHTML='<td colspan="3">Mid</td>',f=v(),A(u.$$.fragment)},m(c,d){g(c,e,d),h(e,r),h(e,n),h(e,o),O(l,o,null),h(o,i),h(o,s),h(o,f),O(u,o,null),a=!0},p(c,d){const _={};d&1&&(_.instrumentCode=c[0]),d&2&&(_.bookEntryList=c[1].asks.reverse()),l.$set(_);const m={};d&1&&(m.instrumentCode=c[0]),d&2&&(m.bookEntryList=c[1].bids.reverse()),u.$set(m)},i(c){a||(C(l.$$.fragment,c),C(u.$$.fragment,c),a=!0)},o(c){L(l.$$.fragment,c),L(u.$$.fragment,c),a=!1},d(c){c&&$(e),S(l),S(u)}}}function Ze(t){let e,r,n=t[1]!=null&&$e(t);return{c(){n&&n.c(),e=V()},m(o,l){n&&n.m(o,l),g(o,e,l),r=!0},p(o,[l]){o[1]!=null?n?(n.p(o,l),l&2&&C(n,1)):(n=$e(o),n.c(),C(n,1),n.m(e.parentNode,e)):n&&(Ce(),L(n,1,1,()=>{n=null}),Le())},i(o){r||(C(n),r=!0)},o(o){L(n),r=!1},d(o){o&&$(e),n&&n.d(o)}}}function xe(t,e,r){let n,{instrumentCode:o}=e;function l(){fetch(`book?instrumentCode=${o}`).then(f=>f.json()).then(f=>{r(1,n=f)})}re(()=>{let f=new WebSocket("ws://localhost:8081/market-data?instrumentCode="+o);f.onmessage=()=>{console.log("ok")},f.addEventListener("open",()=>{console.log("Opened")})});function i(f){fe.call(this,t,f)}function s(f){fe.call(this,t,f)}return t.$$set=f=>{"instrumentCode"in f&&r(0,o=f.instrumentCode)},[o,n,l,i,s]}class et extends B{constructor(e){super(),T(this,e,xe,Ze,D,{instrumentCode:0,refresh:2})}get refresh(){return this.$$.ctx[2]}}function tt(t){let e,r,n;return{c(){e=p("form"),e.innerHTML='<label>Instrument<input type="text" name="instrumentCode" placeholder="SPY"/></label> <label>Amount<input type="text" name="amount" placeholder="10"/></label> <label>Price<input type="text" name="price" placeholder="$100"/></label> <input type="submit" name="buy" value="Buy"/> <input type="submit" name="sell" value="Sell"/>',k(e,"method","post"),k(e,"action","/orders/submit")},m(o,l){g(o,e,l),r||(n=te(e,"submit",ne(t[0])),r=!0)},p:y,i:y,o:y,d(o){o&&$(e),r=!1,n()}}}function nt(t){const e=oe();return[n=>{const o=n.target.action,l=new FormData(n.target),i=new URLSearchParams;for(let f of l){const[u,a]=f;i.append(u,a)}let s=n.submitter.name==="buy"?"Buy":"Sell";i.append("side",s),fetch(o,{method:"POST",body:i}).then(f=>{n.target.reset(),e("submit-order",{})})}]}class rt extends B{constructor(e){super(),T(this,e,nt,tt,D,{})}}function ot(t){let e;return{c(){e=p("a"),e.textContent="Login with Google",k(e,"href","/oauth2/authorization/google")},m(r,n){g(r,e,n)},p:y,i:y,o:y,d(r){r&&$(e)}}}function lt(t){let e,r,n,o,l,i,s,f,u,a,c,d,_,m,w,P;function R(b){t[6](b)}let le={instrumentCode:"BTC/USD"};t[2]!==void 0&&(le.refresh=t[2]),n=new et({props:le}),W.push(()=>ee(n,"refresh",R)),n.$on("order-canceled",t[4]),s=new rt({}),s.$on("submit-order",t[4]);function Ee(b){t[7](b)}let ie={};return t[1]!==void 0&&(ie.refresh=t[1]),c=new He({props:ie}),W.push(()=>ee(c,"refresh",Ee)),w=new Ve({}),w.$on("deposit",t[3]),{c(){e=p("div"),r=p("div"),A(n.$$.fragment),l=v(),i=p("div"),A(s.$$.fragment),f=v(),u=p("div"),a=p("div"),A(c.$$.fragment),_=v(),m=p("div"),A(w.$$.fragment),k(e,"class","row"),k(u,"class","row")},m(b,M){g(b,e,M),h(e,r),O(n,r,null),h(e,l),h(e,i),O(s,i,null),g(b,f,M),g(b,u,M),h(u,a),O(c,a,null),h(u,_),h(u,m),O(w,m,null),P=!0},p(b,M){const se={};!o&&M&4&&(o=!0,se.refresh=b[2],x(()=>o=!1)),n.$set(se);const ue={};!d&&M&2&&(d=!0,ue.refresh=b[1],x(()=>d=!1)),c.$set(ue)},i(b){P||(C(n.$$.fragment,b),C(s.$$.fragment,b),C(c.$$.fragment,b),C(w.$$.fragment,b),P=!0)},o(b){L(n.$$.fragment,b),L(s.$$.fragment,b),L(c.$$.fragment,b),L(w.$$.fragment,b),P=!1},d(b){b&&($(e),$(f),$(u)),S(n),S(s),S(c),S(w)}}}function it(t){let e,r,n,o,l,i,s,f;function u(m){t[5](m)}let a={};t[0]!==void 0&&(a.user=t[0]),n=new qe({props:a}),W.push(()=>ee(n,"user",u));const c=[lt,ot],d=[];function _(m,w){return m[0]!=null?0:1}return i=_(t),s=d[i]=c[i](t),{c(){e=p("main"),r=p("header"),A(n.$$.fragment),l=v(),s.c()},m(m,w){g(m,e,w),h(e,r),O(n,r,null),h(e,l),d[i].m(e,null),f=!0},p(m,[w]){const P={};!o&&w&1&&(o=!0,P.user=m[0],x(()=>o=!1)),n.$set(P);let R=i;i=_(m),i===R?d[i].p(m,w):(Ce(),L(d[R],1,1,()=>{d[R]=null}),Le(),s=d[i],s?s.p(m,w):(s=d[i]=c[i](m),s.c()),C(s,1),s.m(e,null))},i(m){f||(C(n.$$.fragment,m),C(s),f=!0)},o(m){L(n.$$.fragment,m),L(s),f=!1},d(m){m&&$(e),S(n),d[i].d()}}}function st(t,e,r){let n,o,l;function i(){o()}function s(){o(),l()}function f(c){n=c,r(0,n)}function u(c){l=c,r(2,l)}function a(c){o=c,r(1,o)}return[n,o,l,i,s,f,u,a]}class ut extends B{constructor(e){super(),T(this,e,st,it,D,{})}}new ut({target:document.getElementById("app")});