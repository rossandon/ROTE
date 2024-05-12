var ue=Object.defineProperty;var le=(e,t,n)=>t in e?ue(e,t,{enumerable:!0,configurable:!0,writable:!0,value:n}):e[t]=n;var H=(e,t,n)=>(le(e,typeof t!="symbol"?t+"":t,n),n);(function(){const t=document.createElement("link").relList;if(t&&t.supports&&t.supports("modulepreload"))return;for(const o of document.querySelectorAll('link[rel="modulepreload"]'))r(o);new MutationObserver(o=>{for(const s of o)if(s.type==="childList")for(const i of s.addedNodes)i.tagName==="LINK"&&i.rel==="modulepreload"&&r(i)}).observe(document,{childList:!0,subtree:!0});function n(o){const s={};return o.integrity&&(s.integrity=o.integrity),o.referrerPolicy&&(s.referrerPolicy=o.referrerPolicy),o.crossOrigin==="use-credentials"?s.credentials="include":o.crossOrigin==="anonymous"?s.credentials="omit":s.credentials="same-origin",s}function r(o){if(o.ep)return;o.ep=!0;const s=n(o);fetch(o.href,s)}})();function m(){}function ee(e){return e()}function J(){return Object.create(null)}function P(e){e.forEach(ee)}function te(e){return typeof e=="function"}function M(e,t){return e!=e?t==t:e!==t||e&&typeof e=="object"||typeof e=="function"}function fe(e){return Object.keys(e).length===0}function p(e,t){e.appendChild(t)}function $(e,t,n){e.insertBefore(t,n||null)}function g(e){e.parentNode&&e.parentNode.removeChild(e)}function ce(e,t){for(let n=0;n<e.length;n+=1)e[n]&&e[n].d(t)}function _(e){return document.createElement(e)}function w(e){return document.createTextNode(e)}function D(){return w(" ")}function ae(){return w("")}function de(e,t,n,r){return e.addEventListener(t,n,r),()=>e.removeEventListener(t,n,r)}function he(e){return function(t){return t.preventDefault(),e.call(this,t)}}function N(e,t,n){n==null?e.removeAttribute(t):e.getAttribute(t)!==n&&e.setAttribute(t,n)}function pe(e){return Array.from(e.childNodes)}function z(e,t){t=""+t,e.data!==t&&(e.data=t)}function me(e,t,{bubbles:n=!1,cancelable:r=!1}={}){return new CustomEvent(e,{detail:t,bubbles:n,cancelable:r})}let C;function k(e){C=e}function ne(){if(!C)throw new Error("Function called outside component initialization");return C}function re(e){ne().$$.on_mount.push(e)}function _e(){const e=ne();return(t,n,{cancelable:r=!1}={})=>{const o=e.$$.callbacks[t];if(o){const s=me(t,n,{cancelable:r});return o.slice().forEach(i=>{i.call(e,s)}),!s.defaultPrevented}return!0}}const x=[],j=[];let O=[];const q=[],ge=Promise.resolve();let G=!1;function $e(){G||(G=!0,ge.then(se))}function K(e){O.push(e)}function oe(e){q.push(e)}const R=new Set;let E=0;function se(){if(E!==0)return;const e=C;do{try{for(;E<x.length;){const t=x[E];E++,k(t),be(t.$$)}}catch(t){throw x.length=0,E=0,t}for(k(null),x.length=0,E=0;j.length;)j.pop()();for(let t=0;t<O.length;t+=1){const n=O[t];R.has(n)||(R.add(n),n())}O.length=0}while(x.length);for(;q.length;)q.pop()();G=!1,R.clear(),k(e)}function be(e){if(e.fragment!==null){e.update(),P(e.before_update);const t=e.dirty;e.dirty=[-1],e.fragment&&e.fragment.p(e.ctx,t),e.after_update.forEach(K)}}function ye(e){const t=[],n=[];O.forEach(r=>e.indexOf(r)===-1?t.push(r):n.push(r)),n.forEach(r=>r()),O=t}const S=new Set;let y;function we(){y={r:0,c:[],p:y}}function ve(){y.r||P(y.c),y=y.p}function L(e,t){e&&e.i&&(S.delete(e),e.i(t))}function A(e,t,n,r){if(e&&e.o){if(S.has(e))return;S.add(e),y.c.push(()=>{S.delete(e),r&&(n&&e.d(1),r())}),e.o(t)}else r&&r()}function Q(e){return(e==null?void 0:e.length)!==void 0?e:Array.from(e)}function ie(e,t,n){const r=e.$$.props[t];r!==void 0&&(e.$$.bound[r]=n,n(e.$$.ctx[r]))}function V(e){e&&e.c()}function B(e,t,n){const{fragment:r,after_update:o}=e.$$;r&&r.m(t,n),K(()=>{const s=e.$$.on_mount.map(ee).filter(te);e.$$.on_destroy?e.$$.on_destroy.push(...s):P(s),e.$$.on_mount=[]}),o.forEach(K)}function I(e,t){const n=e.$$;n.fragment!==null&&(ye(n.after_update),P(n.on_destroy),n.fragment&&n.fragment.d(t),n.on_destroy=n.fragment=null,n.ctx=[])}function Ee(e,t){e.$$.dirty[0]===-1&&(x.push(e),$e(),e.$$.dirty.fill(0)),e.$$.dirty[t/31|0]|=1<<t%31}function T(e,t,n,r,o,s,i=null,u=[-1]){const l=C;k(e);const c=e.$$={fragment:null,ctx:[],props:s,update:m,not_equal:o,bound:J(),on_mount:[],on_destroy:[],on_disconnect:[],before_update:[],after_update:[],context:new Map(t.context||(l?l.$$.context:[])),callbacks:J(),dirty:u,skip_bound:!1,root:t.target||l.$$.root};i&&i(c.root);let d=!1;if(c.ctx=n?n(e,t.props||{},(f,h,...b)=>{const a=b.length?b[0]:h;return c.ctx&&o(c.ctx[f],c.ctx[f]=a)&&(!c.skip_bound&&c.bound[f]&&c.bound[f](a),d&&Ee(e,f)),h}):[],c.update(),d=!0,P(c.before_update),c.fragment=r?r(c.ctx):!1,t.target){if(t.hydrate){const f=pe(t.target);c.fragment&&c.fragment.l(f),f.forEach(g)}else c.fragment&&c.fragment.c();t.intro&&L(e.$$.fragment),B(e,t.target,t.anchor),se()}k(l)}class U{constructor(){H(this,"$$");H(this,"$$set")}$destroy(){I(this,1),this.$destroy=m}$on(t,n){if(!te(n))return m;const r=this.$$.callbacks[t]||(this.$$.callbacks[t]=[]);return r.push(n),()=>{const o=r.indexOf(n);o!==-1&&r.splice(o,1)}}$set(t){this.$$set&&!fe(t)&&(this.$$.skip_bound=!0,this.$$set(t),this.$$.skip_bound=!1)}}const xe="4";typeof window<"u"&&(window.__svelte||(window.__svelte={v:new Set})).v.add(xe);function X(e,t,n){const r=e.slice();return r[2]=t[n],r}function Y(e){let t,n,r=e[2]+"",o,s,i,u=e[0][e[2]]+"",l,c;return{c(){t=_("tr"),n=_("td"),o=w(r),s=D(),i=_("td"),l=w(u),c=D()},m(d,f){$(d,t,f),p(t,n),p(n,o),p(t,s),p(t,i),p(i,l),p(t,c)},p(d,f){f&1&&r!==(r=d[2]+"")&&z(o,r),f&1&&u!==(u=d[0][d[2]]+"")&&z(l,u)},d(d){d&&g(t)}}}function Oe(e){let t,n,r,o=Q(Object.keys(e[0])),s=[];for(let i=0;i<o.length;i+=1)s[i]=Y(X(e,o,i));return{c(){t=_("table"),n=_("tr"),n.innerHTML='<th style="min-width: 200px">Asset</th> <th>Balance</th>',r=D();for(let i=0;i<s.length;i+=1)s[i].c()},m(i,u){$(i,t,u),p(t,n),p(t,r);for(let l=0;l<s.length;l+=1)s[l]&&s[l].m(t,null)},p(i,[u]){if(u&1){o=Q(Object.keys(i[0]));let l;for(l=0;l<o.length;l+=1){const c=X(i,o,l);s[l]?s[l].p(c,u):(s[l]=Y(c),s[l].c(),s[l].m(t,null))}for(;l<s.length;l+=1)s[l].d(1);s.length=o.length}},i:m,o:m,d(i){i&&g(t),ce(s,i)}}}function Le(e,t,n){let r={};function o(){fetch("balances/list").then(s=>s.json()).then(s=>n(0,r=s))}return re(()=>{o()}),[r,o]}class ke extends U{constructor(t){super(),T(this,t,Le,Oe,M,{refresh:1})}get refresh(){return this.$$.ctx[1]}}function Z(e){let t,n=e[0].name+"",r,o,s;return{c(){t=w("Hello, "),r=w(n),o=w(". "),s=_("a"),s.textContent="Logout",N(s,"href","/logout")},m(i,u){$(i,t,u),$(i,r,u),$(i,o,u),$(i,s,u)},p(i,u){u&1&&n!==(n=i[0].name+"")&&z(r,n)},d(i){i&&(g(t),g(r),g(o),g(s))}}}function Ae(e){let t,n=e[0]!=null&&Z(e);return{c(){n&&n.c(),t=ae()},m(r,o){n&&n.m(r,o),$(r,t,o)},p(r,[o]){r[0]!=null?n?n.p(r,o):(n=Z(r),n.c(),n.m(t.parentNode,t)):n&&(n.d(1),n=null)},i:m,o:m,d(r){r&&g(t),n&&n.d(r)}}}async function De(){try{const e=await fetch("system/whoami");if(!e.ok)throw new Error(`Error! status: ${e.status}`);return await e.json()}catch(e){return e instanceof Error?(console.log("error message: ",e.message),null):(console.log("unexpected error: ",e),null)}}function Ne(e,t,n){let{user:r=null}=t;return re(async()=>{n(0,r=await De())}),e.$$set=o=>{"user"in o&&n(0,r=o.user)},[r]}class Ce extends U{constructor(t){super(),T(this,t,Ne,Ae,M,{user:0})}}function Pe(e){let t,n,r;return{c(){t=_("form"),t.innerHTML='<label>Asset<input type="text" name="assetCode" placeholder="USD"/></label> <label>Amount<input type="text" name="amount" placeholder="100"/></label> <input type="submit" value="Deposit"/>',N(t,"method","post"),N(t,"action","/balances/deposit")},m(o,s){$(o,t,s),n||(r=de(t,"submit",he(e[0])),n=!0)},p:m,i:m,o:m,d(o){o&&g(t),n=!1,r()}}}function Se(e){const t=_e();return[r=>{const o=r.target.action,s=new FormData(r.target),i=new URLSearchParams;for(let u of s){const[l,c]=u;i.append(l,c)}fetch(o,{method:"POST",body:i}).then(u=>{r.target.reset(),t("deposit",{})})}]}class je extends U{constructor(t){super(),T(this,t,Se,Pe,M,{})}}function Be(e){let t;return{c(){t=_("a"),t.textContent="Login with Google",N(t,"href","/oauth2/authorization/google")},m(n,r){$(n,t,r)},p:m,i:m,o:m,d(n){n&&g(t)}}}function Ie(e){let t,n,r,o,s,i,u,l;function c(f){e[4](f)}let d={};return e[1]!==void 0&&(d.refresh=e[1]),r=new ke({props:d}),j.push(()=>ie(r,"refresh",c)),u=new je({}),u.$on("deposit",e[2]),{c(){t=_("div"),n=_("div"),V(r.$$.fragment),s=D(),i=_("div"),V(u.$$.fragment),N(t,"class","test")},m(f,h){$(f,t,h),p(t,n),B(r,n,null),p(t,s),p(t,i),B(u,i,null),l=!0},p(f,h){const b={};!o&&h&2&&(o=!0,b.refresh=f[1],oe(()=>o=!1)),r.$set(b)},i(f){l||(L(r.$$.fragment,f),L(u.$$.fragment,f),l=!0)},o(f){A(r.$$.fragment,f),A(u.$$.fragment,f),l=!1},d(f){f&&g(t),I(r),I(u)}}}function Me(e){let t,n,r,o,s,i,u,l;function c(a){e[3](a)}let d={};e[0]!==void 0&&(d.user=e[0]),r=new Ce({props:d}),j.push(()=>ie(r,"user",c));const f=[Ie,Be],h=[];function b(a,v){return a[0]!=null?0:1}return i=b(e),u=h[i]=f[i](e),{c(){t=_("main"),n=_("header"),V(r.$$.fragment),s=D(),u.c()},m(a,v){$(a,t,v),p(t,n),B(r,n,null),p(t,s),h[i].m(t,null),l=!0},p(a,[v]){const W={};!o&&v&1&&(o=!0,W.user=a[0],oe(()=>o=!1)),r.$set(W);let F=i;i=b(a),i===F?h[i].p(a,v):(we(),A(h[F],1,1,()=>{h[F]=null}),ve(),u=h[i],u?u.p(a,v):(u=h[i]=f[i](a),u.c()),L(u,1),u.m(t,null))},i(a){l||(L(r.$$.fragment,a),L(u),l=!0)},o(a){A(r.$$.fragment,a),A(u),l=!1},d(a){a&&g(t),I(r),h[i].d()}}}function Te(e,t,n){let r,o;function s(){o()}function i(l){r=l,n(0,r)}function u(l){o=l,n(1,o)}return[r,o,s,i,u]}class Ue extends U{constructor(t){super(),T(this,t,Te,Me,M,{})}}new Ue({target:document.getElementById("app")});