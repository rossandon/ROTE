var Ye=Object.defineProperty;var Ge=(t,e,n)=>e in t?Ye(t,e,{enumerable:!0,configurable:!0,writable:!0,value:n}):t[e]=n;var fe=(t,e,n)=>(Ge(t,typeof e!="symbol"?e+"":e,n),n);(function(){const e=document.createElement("link").relList;if(e&&e.supports&&e.supports("modulepreload"))return;for(const r of document.querySelectorAll('link[rel="modulepreload"]'))l(r);new MutationObserver(r=>{for(const o of r)if(o.type==="childList")for(const s of o.addedNodes)s.tagName==="LINK"&&s.rel==="modulepreload"&&l(s)}).observe(document,{childList:!0,subtree:!0});function n(r){const o={};return r.integrity&&(o.integrity=r.integrity),r.referrerPolicy&&(o.referrerPolicy=r.referrerPolicy),r.crossOrigin==="use-credentials"?o.credentials="include":r.crossOrigin==="anonymous"?o.credentials="omit":o.credentials="same-origin",o}function l(r){if(r.ep)return;r.ep=!0;const o=n(r);fetch(r.href,o)}})();function L(){}function Fe(t){return t()}function Te(){return Object.create(null)}function x(t){t.forEach(Fe)}function Je(t){return typeof t=="function"}function z(t,e){return t!=t?e==e:t!==e||t&&typeof t=="object"||typeof t=="function"}function Qe(t){return Object.keys(t).length===0}function a(t,e){t.appendChild(e)}function y(t,e,n){t.insertBefore(e,n||null)}function $(t){t.parentNode&&t.parentNode.removeChild(t)}function _e(t,e){for(let n=0;n<t.length;n+=1)t[n]&&t[n].d(e)}function m(t){return document.createElement(t)}function E(t){return document.createTextNode(t)}function g(){return E(" ")}function ee(){return E("")}function ge(t,e,n,l){return t.addEventListener(e,n,l),()=>t.removeEventListener(e,n,l)}function ve(t){return function(e){return e.preventDefault(),t.call(this,e)}}function p(t,e,n){n==null?t.removeAttribute(e):t.getAttribute(e)!==n&&t.setAttribute(e,n)}function Xe(t){return Array.from(t.childNodes)}function T(t,e){e=""+e,t.data!==e&&(t.data=e)}function de(t,e,n,l){n==null?t.style.removeProperty(e):t.style.setProperty(e,n,l?"important":"")}function Ze(t,e,{bubbles:n=!1,cancelable:l=!1}={}){return new CustomEvent(t,{detail:e,bubbles:n,cancelable:l})}let Z;function X(t){Z=t}function We(){if(!Z)throw new Error("Function called outside component initialization");return Z}function ce(t){We().$$.on_mount.push(t)}function $e(){const t=We();return(e,n,{cancelable:l=!1}={})=>{const r=t.$$.callbacks[e];if(r){const o=Ze(e,n,{cancelable:l});return r.slice().forEach(s=>{s.call(t,o)}),!o.defaultPrevented}return!0}}function Se(t,e){const n=t.$$.callbacks[e.type];n&&n.slice().forEach(l=>l.call(this,e))}const Y=[],ae=[];let G=[];const pe=[],xe=Promise.resolve();let he=!1;function et(){he||(he=!0,xe.then(qe))}function be(t){G.push(t)}function Ne(t){pe.push(t)}const me=new Set;let V=0;function qe(){if(V!==0)return;const t=Z;do{try{for(;V<Y.length;){const e=Y[V];V++,X(e),tt(e.$$)}}catch(e){throw Y.length=0,V=0,e}for(X(null),Y.length=0,V=0;ae.length;)ae.pop()();for(let e=0;e<G.length;e+=1){const n=G[e];me.has(n)||(me.add(n),n())}G.length=0}while(Y.length);for(;pe.length;)pe.pop()();he=!1,me.clear(),X(t)}function tt(t){if(t.fragment!==null){t.update(),x(t.before_update);const e=t.dirty;t.dirty=[-1],t.fragment&&t.fragment.p(t.ctx,e),t.after_update.forEach(be)}}function nt(t){const e=[],n=[];G.forEach(l=>t.indexOf(l)===-1?e.push(l):n.push(l)),n.forEach(l=>l()),G=e}const ie=new Set;let q;function lt(){q={r:0,c:[],p:q}}function rt(){q.r||x(q.c),q=q.p}function N(t,e){t&&t.i&&(ie.delete(t),t.i(e))}function P(t,e,n,l){if(t&&t.o){if(ie.has(t))return;ie.add(t),q.c.push(()=>{ie.delete(t),l&&(n&&t.d(1),l())}),t.o(e)}else l&&l()}function Q(t){return(t==null?void 0:t.length)!==void 0?t:Array.from(t)}function Pe(t,e,n){const l=t.$$.props[e];l!==void 0&&(t.$$.bound[l]=n,n(t.$$.ctx[l]))}function j(t){t&&t.c()}function M(t,e,n){const{fragment:l,after_update:r}=t.$$;l&&l.m(e,n),be(()=>{const o=t.$$.on_mount.map(Fe).filter(Je);t.$$.on_destroy?t.$$.on_destroy.push(...o):x(o),t.$$.on_mount=[]}),r.forEach(be)}function B(t,e){const n=t.$$;n.fragment!==null&&(nt(n.after_update),x(n.on_destroy),n.fragment&&n.fragment.d(e),n.on_destroy=n.fragment=null,n.ctx=[])}function st(t,e){t.$$.dirty[0]===-1&&(Y.push(t),et(),t.$$.dirty.fill(0)),t.$$.dirty[e/31|0]|=1<<e%31}function I(t,e,n,l,r,o,s=null,c=[-1]){const d=Z;X(t);const i=t.$$={fragment:null,ctx:[],props:o,update:L,not_equal:r,bound:Te(),on_mount:[],on_destroy:[],on_disconnect:[],before_update:[],after_update:[],context:new Map(e.context||(d?d.$$.context:[])),callbacks:Te(),dirty:c,skip_bound:!1,root:e.target||d.$$.root};s&&s(i.root);let u=!1;if(i.ctx=n?n(t,e.props||{},(f,h,..._)=>{const b=_.length?_[0]:h;return i.ctx&&r(i.ctx[f],i.ctx[f]=b)&&(!i.skip_bound&&i.bound[f]&&i.bound[f](b),u&&st(t,f)),h}):[],i.update(),u=!0,x(i.before_update),i.fragment=l?l(i.ctx):!1,e.target){if(e.hydrate){const f=Xe(e.target);i.fragment&&i.fragment.l(f),f.forEach($)}else i.fragment&&i.fragment.c();e.intro&&N(t.$$.fragment),M(t,e.target,e.anchor),qe()}X(d)}class R{constructor(){fe(this,"$$");fe(this,"$$set")}$destroy(){B(this,1),this.$destroy=L}$on(e,n){if(!Je(n))return L;const l=this.$$.callbacks[e]||(this.$$.callbacks[e]=[]);return l.push(n),()=>{const r=l.indexOf(n);r!==-1&&l.splice(r,1)}}$set(e){this.$$set&&!Qe(e)&&(this.$$.skip_bound=!0,this.$$set(e),this.$$.skip_bound=!1)}}const ot="4";typeof window<"u"&&(window.__svelte||(window.__svelte={v:new Set})).v.add(ot);function De(t,e,n){const l=t.slice();return l[2]=e[n],l}function Me(t){let e,n,l=t[2]+"",r,o,s,c=t[0][t[2]]+"",d,i;return{c(){e=m("tr"),n=m("td"),r=E(l),o=g(),s=m("td"),d=E(c),i=g()},m(u,f){y(u,e,f),a(e,n),a(n,r),a(e,o),a(e,s),a(s,d),a(e,i)},p(u,f){f&1&&l!==(l=u[2]+"")&&T(r,l),f&1&&c!==(c=u[0][u[2]]+"")&&T(d,c)},d(u){u&&$(e)}}}function it(t){let e,n,l,r,o=Q(Object.keys(t[0])),s=[];for(let c=0;c<o.length;c+=1)s[c]=Me(De(t,o,c));return{c(){e=m("h5"),e.innerHTML='<span class="p-1 border-bottom">Balances</span>',n=g(),l=m("table"),r=m("tbody");for(let c=0;c<s.length;c+=1)s[c].c();p(e,"class","text-center"),p(l,"class","table")},m(c,d){y(c,e,d),y(c,n,d),y(c,l,d),a(l,r);for(let i=0;i<s.length;i+=1)s[i]&&s[i].m(r,null)},p(c,[d]){if(d&1){o=Q(Object.keys(c[0]));let i;for(i=0;i<o.length;i+=1){const u=De(c,o,i);s[i]?s[i].p(u,d):(s[i]=Me(u),s[i].c(),s[i].m(r,null))}for(;i<s.length;i+=1)s[i].d(1);s.length=o.length}},i:L,o:L,d(c){c&&($(e),$(n),$(l)),_e(s,c)}}}function at(t,e,n){let l={};function r(){fetch("balances/list").then(o=>o.json()).then(o=>n(0,l=o))}return ce(()=>{r()}),[l,r]}class ct extends R{constructor(e){super(),I(this,e,at,it,z,{refresh:1})}get refresh(){return this.$$.ctx[1]}}function Be(t){let e,n,l=t[0].name+"",r,o,s,c;return{c(){e=m("span"),n=E("Hello, "),r=E(l),o=E("."),s=g(),c=m("a"),c.innerHTML='<button type="button" class="btn btn-outline-light me-2">Logout</button>',p(e,"class","px-3"),p(c,"href","/logout")},m(d,i){y(d,e,i),a(e,n),a(e,r),a(e,o),y(d,s,i),y(d,c,i)},p(d,i){i&1&&l!==(l=d[0].name+"")&&T(r,l)},d(d){d&&($(e),$(s),$(c))}}}function ut(t){let e,n=t[0]!=null&&Be(t);return{c(){n&&n.c(),e=ee()},m(l,r){n&&n.m(l,r),y(l,e,r)},p(l,[r]){l[0]!=null?n?n.p(l,r):(n=Be(l),n.c(),n.m(e.parentNode,e)):n&&(n.d(1),n=null)},i:L,o:L,d(l){l&&$(e),n&&n.d(l)}}}async function ft(){try{const t=await fetch("system/whoami");if(!t.ok)throw new Error(`Error! status: ${t.status}`);return await t.json()}catch(t){return t instanceof Error?(console.log("error message: ",t.message),null):(console.log("unexpected error: ",t),null)}}function dt(t,e,n){let{user:l=null}=e;return ce(async()=>{n(0,l=await ft())}),t.$$set=r=>{"user"in r&&n(0,l=r.user)},[l]}class mt extends R{constructor(e){super(),I(this,e,dt,ut,z,{user:0})}}function pt(t){let e,n,l,r,o;return{c(){e=m("h5"),e.innerHTML='<span class="p-1 border-bottom" style="width: 200px">Deposit</span>',n=g(),l=m("form"),l.innerHTML='<div class="mb-3"><label for="deposit-asset" class="form-label">Asset</label><input class="form-control" id="deposit-asset" type="text" name="assetCode" placeholder="USD"/></div> <div class="mb-3"><label for="deposit-amount" class="form-label">Amount</label> <input class="form-control" id="deposit-amount" type="text" name="amount" placeholder="100"/></div> <input class="btn btn-primary" type="submit" value="Deposit"/>',p(e,"class","text-center"),p(l,"method","post"),p(l,"action","/balances/deposit")},m(s,c){y(s,e,c),y(s,n,c),y(s,l,c),r||(o=ge(l,"submit",ve(t[0])),r=!0)},p:L,i:L,o:L,d(s){s&&($(e),$(n),$(l)),r=!1,o()}}}function ht(t){const e=$e();return[l=>{const r=l.target.action,o=new FormData(l.target),s=new URLSearchParams;for(let c of o){const[d,i]=c;s.append(d,i)}fetch(r,{method:"POST",body:s}).then(c=>{l.target.reset(),e("deposit",{})})}]}class bt extends R{constructor(e){super(),I(this,e,ht,pt,z,{})}}function Ae(t,e,n){const l=t.slice();return l[4]=e[n],l}function _t(t){let e,n=Q(t[0]),l=[];for(let r=0;r<n.length;r+=1)l[r]=Ue(Ae(t,n,r));return{c(){for(let r=0;r<l.length;r+=1)l[r].c();e=ee()},m(r,o){for(let s=0;s<l.length;s+=1)l[s]&&l[s].m(r,o);y(r,e,o)},p(r,o){if(o&7){n=Q(r[0]);let s;for(s=0;s<n.length;s+=1){const c=Ae(r,n,s);l[s]?l[s].p(c,o):(l[s]=Ue(c),l[s].c(),l[s].m(e.parentNode,e))}for(;s<l.length;s+=1)l[s].d(1);l.length=n.length}},d(r){r&&$(e),_e(l,r)}}}function gt(t){let e;return{c(){e=m("tr"),e.innerHTML='<td colspan="3">No entries.</td>'},m(n,l){y(n,e,l)},p:L,d(n){n&&$(e)}}}function vt(t){let e,n,l=t[4].price+"",r,o,s,c=t[4].size+"",d,i,u,f,h=t[4].canCancel&&He(t);return{c(){e=m("tr"),n=m("td"),r=E(l),o=g(),s=m("td"),d=E(c),i=g(),u=m("td"),h&&h.c(),f=g()},m(_,b){y(_,e,b),a(e,n),a(n,r),a(e,o),a(e,s),a(s,d),a(e,i),a(e,u),h&&h.m(u,null),a(e,f)},p(_,b){b&1&&l!==(l=_[4].price+"")&&T(r,l),b&1&&c!==(c=_[4].size+"")&&T(d,c),_[4].canCancel?h?h.p(_,b):(h=He(_),h.c(),h.m(u,null)):h&&(h.d(1),h=null)},d(_){_&&$(e),h&&h.d()}}}function $t(t){let e;return{c(){e=m("tr"),e.innerHTML='<td colspan="3">-</td> '},m(n,l){y(n,e,l)},p:L,d(n){n&&$(e)}}}function He(t){let e,n,l,r,o,s,c,d,i;return{c(){e=m("form"),n=m("input"),r=g(),o=m("input"),s=g(),c=m("button"),c.textContent="Cancel",p(n,"type","hidden"),p(n,"name","id"),n.value=l=t[4].id,p(o,"type","hidden"),p(o,"name","instrumentCode"),o.value=t[1],p(c,"class","btn btn-primary"),de(c,"--bs-btn-padding-y",".1rem"),de(c,"--bs-btn-padding-x",".1rem"),de(c,"--bs-btn-font-size",".60rem"),p(c,"type","submit"),c.value="Cancel",p(e,"class","order-cancel"),p(e,"method","post"),p(e,"action","/orders/cancel")},m(u,f){y(u,e,f),a(e,n),a(e,r),a(e,o),a(e,s),a(e,c),d||(i=ge(e,"submit",ve(t[2])),d=!0)},p(u,f){f&1&&l!==(l=u[4].id)&&(n.value=l),f&2&&(o.value=u[1])},d(u){u&&$(e),d=!1,i()}}}function Ue(t){let e;function n(o,s){return o[4]==null?$t:vt}let l=n(t),r=l(t);return{c(){r.c(),e=ee()},m(o,s){r.m(o,s),y(o,e,s)},p(o,s){l===(l=n(o))&&r?r.p(o,s):(r.d(1),r=l(o),r&&(r.c(),r.m(e.parentNode,e)))},d(o){o&&$(e),r.d(o)}}}function yt(t){let e;function n(o,s){return o[0].length===0?gt:_t}let l=n(t),r=l(t);return{c(){r.c(),e=ee()},m(o,s){r.m(o,s),y(o,e,s)},p(o,[s]){l===(l=n(o))&&r?r.p(o,s):(r.d(1),r=l(o),r&&(r.c(),r.m(e.parentNode,e)))},i:L,o:L,d(o){o&&$(e),r.d(o)}}}function wt(t,e,n){const l=$e();let{bookEntryList:r}=e,{instrumentCode:o}=e;const s=c=>{const d=c.target.action,i=new FormData(c.target),u=new URLSearchParams;for(let f of i){const[h,_]=f;u.append(h,_)}fetch(d,{method:"POST",body:u}).then(f=>{l("order-canceled")})};return t.$$set=c=>{"bookEntryList"in c&&n(0,r=c.bookEntryList),"instrumentCode"in c&&n(1,o=c.instrumentCode)},[r,o,s]}class je extends R{constructor(e){super(),I(this,e,wt,yt,z,{bookEntryList:0,instrumentCode:1})}}function ze(t){let e,n,l,r,o,s,c,d,i,u;return o=new je({props:{instrumentCode:t[0],bookEntryList:t[2]}}),o.$on("order-canceled",t[4]),i=new je({props:{instrumentCode:t[0],bookEntryList:t[1]}}),i.$on("order-canceled",t[5]),{c(){e=m("table"),n=m("thead"),n.innerHTML="<tr><th>Price</th> <th>Size</th> <th></th></tr>",l=g(),r=m("tbody"),j(o.$$.fragment),s=g(),c=m("tr"),c.innerHTML='<td colspan="3">Mid</td>',d=g(),j(i.$$.fragment),p(c,"class","mid"),p(e,"class","order-book table table-sm")},m(f,h){y(f,e,h),a(e,n),a(e,l),a(e,r),M(o,r,null),a(r,s),a(r,c),a(r,d),M(i,r,null),u=!0},p(f,h){const _={};h&1&&(_.instrumentCode=f[0]),h&4&&(_.bookEntryList=f[2]),o.$set(_);const b={};h&1&&(b.instrumentCode=f[0]),h&2&&(b.bookEntryList=f[1]),i.$set(b)},i(f){u||(N(o.$$.fragment,f),N(i.$$.fragment,f),u=!0)},o(f){P(o.$$.fragment,f),P(i.$$.fragment,f),u=!1},d(f){f&&$(e),B(o),B(i)}}}function kt(t){let e,n,l,r,o,s,c,d,i,u=t[1]!=null&&t[2]!=null&&ze(t);return{c(){e=m("div"),n=m("div"),l=m("div"),l.textContent="Order Book",r=g(),o=m("div"),s=E(t[0]),c=g(),d=m("div"),u&&u.c(),p(n,"class","card-header d-flex justify-content-between"),p(d,"class","card-body"),p(e,"class","card")},m(f,h){y(f,e,h),a(e,n),a(n,l),a(n,r),a(n,o),a(o,s),a(e,c),a(e,d),u&&u.m(d,null),i=!0},p(f,[h]){(!i||h&1)&&T(s,f[0]),f[1]!=null&&f[2]!=null?u?(u.p(f,h),h&6&&N(u,1)):(u=ze(f),u.c(),N(u,1),u.m(d,null)):u&&(lt(),P(u,1,1,()=>{u=null}),rt())},i(f){i||(N(u),i=!0)},o(f){P(u),i=!1},d(f){f&&$(e),u&&u.d()}}}function Ct(t,e,n){let l,r,{len:o=5}=e,{instrumentCode:s}=e;function c(u){return u=u.slice(0,o),u=u.concat(new Array(o-u.length)),u}ce(()=>{var u=window.location,f;u.protocol==="https:"?f="wss:":f="ws:",f+="//"+u.host;let h=new WebSocket(f+"/market-data/book?instrumentCode="+s);h.onmessage=_=>{var b=JSON.parse(_.data);n(1,l=c(b.bids)),n(2,r=c(b.asks).reverse())}});function d(u){Se.call(this,t,u)}function i(u){Se.call(this,t,u)}return t.$$set=u=>{"len"in u&&n(3,o=u.len),"instrumentCode"in u&&n(0,s=u.instrumentCode)},[s,l,r,o,d,i]}class Lt extends R{constructor(e){super(),I(this,e,Ct,kt,z,{len:3,instrumentCode:0})}}function Et(t){let e,n,l,r,o,s,c,d,i,u,f,h,_,b,v,C,k,O,A,D;return{c(){e=m("div"),n=m("div"),l=m("div"),l.textContent="Place Order",r=g(),o=m("div"),s=E(t[0]),c=g(),d=m("div"),i=m("form"),u=m("input"),f=g(),h=m("div"),h.innerHTML='<label for="order-amount" class="form-label">Amount</label> <input class="form-control" id="order-amount" type="text" name="amount" placeholder="10"/>',_=g(),b=m("div"),b.innerHTML='<label for="order-price" class="form-label">Price</label> <input class="form-control" id="order-price" type="text" name="price" placeholder="$100"/>',v=g(),C=m("input"),k=g(),O=m("input"),p(n,"class","card-header d-flex justify-content-between"),p(u,"id","order-instrument"),p(u,"type","hidden"),p(u,"name","instrumentCode"),u.value=t[0],p(h,"class","mb-3"),p(b,"class","mb-3"),p(C,"type","submit"),p(C,"name","buy"),C.value="Buy",p(C,"class","btn btn-primary"),p(O,"type","submit"),p(O,"name","sell"),O.value="Sell",p(O,"class","btn btn-primary"),p(i,"method","post"),p(i,"action","/orders/submit"),p(d,"class","card-body"),p(e,"class","card")},m(S,F){y(S,e,F),a(e,n),a(n,l),a(n,r),a(n,o),a(o,s),a(e,c),a(e,d),a(d,i),a(i,u),a(i,f),a(i,h),a(i,_),a(i,b),a(i,v),a(i,C),a(i,k),a(i,O),A||(D=ge(i,"submit",ve(t[1])),A=!0)},p(S,[F]){F&1&&T(s,S[0]),F&1&&(u.value=S[0])},i:L,o:L,d(S){S&&$(e),A=!1,D()}}}function Ot(t,e,n){const l=$e();let{instrumentCode:r}=e;const o=s=>{const c=s.target.action,d=new FormData(s.target),i=new URLSearchParams;for(let f of d){const[h,_]=f;i.append(h,_)}let u=s.submitter.name==="buy"?"Buy":"Sell";i.append("side",u),fetch(c,{method:"POST",body:i}).then(f=>{s.target.reset(),l("submit-order",{})})};return t.$$set=s=>{"instrumentCode"in s&&n(0,r=s.instrumentCode)},[r,o]}class Tt extends R{constructor(e){super(),I(this,e,Ot,Et,z,{instrumentCode:0})}}function Ie(t,e,n){const l=t.slice();return l[3]=e[n],l}function St(t){let e,n,l=t[3].price+"",r,o,s,c=t[3].size+"",d,i,u,f=t[3].timestamp+"",h,_;return{c(){e=m("tr"),n=m("td"),r=E(l),o=g(),s=m("td"),d=E(c),i=g(),u=m("td"),h=E(f),_=g()},m(b,v){y(b,e,v),a(e,n),a(n,r),a(e,o),a(e,s),a(s,d),a(e,i),a(e,u),a(u,h),a(e,_)},p(b,v){v&2&&l!==(l=b[3].price+"")&&T(r,l),v&2&&c!==(c=b[3].size+"")&&T(d,c),v&2&&f!==(f=b[3].timestamp+"")&&T(h,f)},d(b){b&&$(e)}}}function Nt(t){let e,n,l=t[3].price+"",r,o,s,c=t[3].size+"",d,i,u,f=t[3].timestamp+"",h,_;return{c(){e=m("tr"),n=m("td"),r=E(l),o=g(),s=m("td"),d=E(c),i=g(),u=m("td"),h=E(f),_=g(),p(e,"class","table-success")},m(b,v){y(b,e,v),a(e,n),a(n,r),a(e,o),a(e,s),a(s,d),a(e,i),a(e,u),a(u,h),a(e,_)},p(b,v){v&2&&l!==(l=b[3].price+"")&&T(r,l),v&2&&c!==(c=b[3].size+"")&&T(d,c),v&2&&f!==(f=b[3].timestamp+"")&&T(h,f)},d(b){b&&$(e)}}}function Re(t){let e;function n(o,s){return o[3].isYours?Nt:St}let l=n(t),r=l(t);return{c(){r.c(),e=ee()},m(o,s){r.m(o,s),y(o,e,s)},p(o,s){l===(l=n(o))&&r?r.p(o,s):(r.d(1),r=l(o),r&&(r.c(),r.m(e.parentNode,e)))},d(o){o&&$(e),r.d(o)}}}function Pt(t){let e,n,l,r,o,s,c,d,i,u,f,h,_=Q(t[1]),b=[];for(let v=0;v<_.length;v+=1)b[v]=Re(Ie(t,_,v));return{c(){e=m("div"),n=m("div"),l=m("div"),l.textContent="Trades",r=g(),o=m("div"),s=E(t[0]),c=g(),d=m("div"),i=m("table"),u=m("thead"),u.innerHTML="<tr><th>Price</th> <th>Size</th> <th>Time</th></tr>",f=g(),h=m("tbody");for(let v=0;v<b.length;v+=1)b[v].c();p(n,"class","card-header d-flex justify-content-between"),p(i,"class","trades table table-sm"),p(d,"class","card-body"),p(e,"class","card")},m(v,C){y(v,e,C),a(e,n),a(n,l),a(n,r),a(n,o),a(o,s),a(e,c),a(e,d),a(d,i),a(i,u),a(i,f),a(i,h);for(let k=0;k<b.length;k+=1)b[k]&&b[k].m(h,null)},p(v,[C]){if(C&1&&T(s,v[0]),C&2){_=Q(v[1]);let k;for(k=0;k<_.length;k+=1){const O=Ie(v,_,k);b[k]?b[k].p(O,C):(b[k]=Re(O),b[k].c(),b[k].m(h,null))}for(;k<b.length;k+=1)b[k].d(1);b.length=_.length}},i:L,o:L,d(v){v&&$(e),_e(b,v)}}}function Dt(t,e,n){let l=[],{len:r=11}=e,{instrumentCode:o}=e;return ce(()=>{var s=window.location,c;s.protocol==="https:"?c="wss:":c="ws:",c+="//"+s.host;let d,i=new WebSocket(c+"/market-data/trade?instrumentCode="+o);i.onopen=u=>{d=setInterval(()=>{const f=JSON.stringify({ping:1});i.send(f)},5e3)},i.onclose=u=>{clearInterval(d)},i.onmessage=u=>{console.log(u.data);var f=JSON.parse(u.data);n(1,l=l.concat(f)),l.length>r&&n(1,l=l.slice(1))}}),t.$$set=s=>{"len"in s&&n(2,r=s.len),"instrumentCode"in s&&n(0,o=s.instrumentCode)},[o,l,r]}class Mt extends R{constructor(e){super(),I(this,e,Dt,Pt,z,{len:2,instrumentCode:0})}}function Bt(t){let e,n,l,r,o,s,c,d,i,u,f,h,_,b,v,C,k,O,A,D,S,F,te,H,ue,ye,U,ne,J,we,le,W,ke,re,K,se;function Ke(w){t[4](w)}let Ce={};t[1]!==void 0&&(Ce.refresh=t[1]),d=new ct({props:Ce}),ae.push(()=>Pe(d,"refresh",Ke)),C=new bt({}),C.$on("deposit",t[2]);function Ve(w){t[5](w)}let Le={};return t[0]!==void 0&&(Le.user=t[0]),H=new mt({props:Le}),ae.push(()=>Pe(H,"user",Ve)),J=new Lt({props:{instrumentCode:"BTC/USD"}}),J.$on("order-canceled",t[3]),W=new Tt({props:{instrumentCode:"BTC/USD"}}),W.$on("submit-order",t[3]),K=new Mt({props:{instrumentCode:"BTC/USD"}}),{c(){e=m("div"),n=m("div"),l=m("div"),l.innerHTML="",r=g(),o=m("div"),s=m("div"),c=m("div"),j(d.$$.fragment),u=g(),f=m("br"),h=g(),_=m("br"),b=g(),v=m("div"),j(C.$$.fragment),k=g(),O=m("div"),A=m("div"),D=m("header"),S=m("a"),S.innerHTML='<span class="fs-4 app-title">ROTE</span>',F=g(),te=m("div"),j(H.$$.fragment),ye=g(),U=m("div"),ne=m("div"),j(J.$$.fragment),we=g(),le=m("div"),j(W.$$.fragment),ke=g(),re=m("div"),j(K.$$.fragment),p(l,"class","offcanvas-header"),p(c,"class","row"),p(v,"class","row"),p(s,"class","container"),p(o,"class","offcanvas-body"),p(n,"class","offcanvas show offcanvas-start"),p(n,"tabindex","-1"),p(n,"id","offcanvas"),p(n,"aria-labelledby","offcanvasLabel"),p(n,"data-bs-scroll","true"),p(n,"data-bs-backdrop","false"),p(n,"data-bs-keyboard","false"),p(S,"href","/"),p(S,"class","d-flex align-items-center mb-3 mb-md-0 me-md-auto link-body-emphasis text-decoration-none"),p(te,"class","text-end"),p(D,"class","d-flex py-3 mb-4 border-bottom"),p(A,"class","row"),p(ne,"class","col"),p(le,"class","col"),p(re,"class","col"),p(U,"class","row"),p(O,"class","container")},m(w,oe){y(w,e,oe),a(e,n),a(n,l),a(n,r),a(n,o),a(o,s),a(s,c),M(d,c,null),a(s,u),a(s,f),a(s,h),a(s,_),a(s,b),a(s,v),M(C,v,null),a(e,k),a(e,O),a(O,A),a(A,D),a(D,S),a(D,F),a(D,te),M(H,te,null),a(O,ye),a(O,U),a(U,ne),M(J,ne,null),a(U,we),a(U,le),M(W,le,null),a(U,ke),a(U,re),M(K,re,null),se=!0},p(w,[oe]){const Ee={};!i&&oe&2&&(i=!0,Ee.refresh=w[1],Ne(()=>i=!1)),d.$set(Ee);const Oe={};!ue&&oe&1&&(ue=!0,Oe.user=w[0],Ne(()=>ue=!1)),H.$set(Oe)},i(w){se||(N(d.$$.fragment,w),N(C.$$.fragment,w),N(H.$$.fragment,w),N(J.$$.fragment,w),N(W.$$.fragment,w),N(K.$$.fragment,w),se=!0)},o(w){P(d.$$.fragment,w),P(C.$$.fragment,w),P(H.$$.fragment,w),P(J.$$.fragment,w),P(W.$$.fragment,w),P(K.$$.fragment,w),se=!1},d(w){w&&$(e),B(d),B(C),B(H),B(J),B(W),B(K)}}}function At(t,e,n){let l,r;function o(){r()}function s(){r()}function c(i){r=i,n(1,r)}function d(i){l=i,n(0,l)}return[l,r,o,s,c,d]}class Ht extends R{constructor(e){super(),I(this,e,At,Bt,z,{})}}new Ht({target:document.getElementById("app")});