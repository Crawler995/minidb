(this["webpackJsonpminidb-ui"]=this["webpackJsonpminidb-ui"]||[]).push([[0],{174:function(e,t,n){e.exports=n(330)},179:function(e,t,n){},180:function(e,t,n){},208:function(e,t){ace.define("ace/mode/sql_highlight_rules",["require","exports","module","ace/lib/oop","ace/mode/text_highlight_rules"],(function(e,t,n){"use strict";var a=e("../lib/oop"),r=e("./text_highlight_rules").TextHighlightRules,o=function(){var e=this.createKeywordMapper({"support.function":"count",keyword:"select|insert|update|delete|from|where|and|or|group|by|order|limit|offset|left|right|join|on|outer|desc|asc|create|table|not|default|null|inner|database|drop","constant.language":"true|false","storage.type":"int|date|varchar|double|binary"},"identifier",!0);this.$rules={start:[{token:"comment",regex:"--.*$"},{token:"comment",start:"/\\*",end:"\\*/"},{token:"string",regex:'".*?"'},{token:"string",regex:"'.*?'"},{token:"string",regex:"`.*?`"},{token:"constant.numeric",regex:"[+-]?\\d+(?:(?:\\.\\d*)?(?:[eE][+-]?\\d+)?)?\\b"},{token:e,regex:"[a-zA-Z_$][a-zA-Z0-9_$]*\\b"},{token:"keyword.operator",regex:"\\+|\\-|\\/|\\/\\/|%|<@>|@>|<@|&|\\^|~|<|>|<=|=>|==|!=|<>|="},{token:"paren.lparen",regex:"[\\(]"},{token:"paren.rparen",regex:"[\\)]"},{token:"text",regex:"\\s+"}]},this.normalizeRules()};a.inherits(o,r),t.SqlHighlightRules=o})),ace.define("ace/mode/sql",["require","exports","module","ace/lib/oop","ace/mode/text","ace/mode/sql_highlight_rules"],(function(e,t,n){"use strict";var a=e("../lib/oop"),r=e("./text").Mode,o=e("./sql_highlight_rules").SqlHighlightRules,i=function(){this.HighlightRules=o,this.$behaviour=this.$defaultBehaviour};a.inherits(i,r),function(){this.lineCommentStart="--",this.$id="ace/mode/sql"}.call(i.prototype),t.Mode=i}))},330:function(e,t,n){"use strict";n.r(t);var a=n(0),r=n.n(a),o=n(18),i=n.n(o),c=(n(179),n(180),n(59)),l=n(114),u=n(39),s=n(333),d=n(335),f=n(336),m=n(331),h=m.a.Content;function g(e){return r.a.createElement(m.a,{style:{position:"relative",width:"100%",height:"100%"}},r.a.createElement("div",{style:{position:"fixed",zIndex:1,width:"100%",height:40,padding:"0 20px",backgroundColor:"#fff",boxShadow:"0px 0px 30px rgba(0, 0, 0, 0.2)"}},e.header),r.a.createElement(h,{style:{padding:20,marginTop:40,backgroundColor:"#fff"}},e.content))}var b=n(146),p=n.n(b),v=(n(208),n(209),n(210),n(334)),x=n(66);function w(e){var t=Object(a.useState)(e.defaultCode),n=Object(u.a)(t,2),o=n[0],i=n[1],c=Object(a.useState)(""),l=Object(u.a)(c,2),s=l[0],d=l[1],f=Object(a.useState)(0),m=Object(u.a)(f,2),h=m[0],g=m[1];Object(a.useEffect)((function(){i(e.defaultCode)}),[e.defaultCode,e.annotations]);return r.a.createElement(v.a.Ribbon,{text:"SQL Editor"},r.a.createElement("div",{style:{margin:"10px 0"}},r.a.createElement(x.a,{danger:!0,disabled:e.isCodeRunning,size:"small",style:{margin:"0 4px"},onClick:function(){return e.onRunCode(o)}},"Run SQL"),r.a.createElement(x.a,{disabled:e.isCodeRunning,type:"default",size:"small",style:{margin:"0 4px"},onClick:function(){return e.onRunSelectedCode(s,h,o)}},"Run selected SQL"),r.a.createElement(x.a,{type:"default",size:"small",style:{margin:"0 4px"},onClick:function(){return i("")}},"Clear SQL")),r.a.createElement(p.a,{width:"100%",height:"25vh",style:{border:"1px solid #f0f0f0"},fontSize:"16px",mode:"sql",theme:"github",value:o,onChange:function(e){return i(e)},onSelectionChange:function(e){return d(function(e){if(""===o)return"";var t=e=JSON.parse(JSON.stringify(e)),n=t.start,a=t.end;if(g(n.row),n.row===a.row&&n.column===a.column)return"";for(var r=o.split("\n"),i=[],c=n.row;c<=a.row&&!(c>=r.length);c++)if(c!==n.row)c!==a.row?i.push(r[c]):i.push(r[c].substring(0,a.column));else{if(c===a.row){i.push(r[c].substring(n.column,a.column));continue}i.push(r[c].substring(n.column))}return i.join("\n")+"\n"}(e))},annotations:e.annotations,showPrintMargin:!1,editorProps:{$blockScrolling:!0},setOptions:{enableBasicAutocompletion:!0,enableLiveAutocompletion:!0,enableSnippets:!0}}))}var O=n(332),y=[{title:"Status",dataIndex:"status",render:function(e){return e?"\u2714":"\u274c"}},{title:"Message",dataIndex:"message"},{title:"Total time",dataIndex:"totalTime"},{title:"time",dataIndex:"time"}];function E(e){var t=Object(a.useRef)(null);return Object(a.useEffect)((function(){var e,n=null===(e=t.current)||void 0===e?void 0:e.getElementsByClassName("ant-table-body")[0];n&&(n.scrollTop=1e4)}),[e.histories]),r.a.createElement(v.a.Ribbon,{text:"Operation History"},r.a.createElement("div",{style:{margin:"10px 0"}},r.a.createElement(x.a,{type:"default",size:"small",style:{margin:"0 4px"},onClick:function(){return e.onClearHistory()}},"Clear History")),r.a.createElement("div",{ref:t},r.a.createElement(O.a,{className:"op-history",size:"small",pagination:!1,columns:y,dataSource:e.histories,scroll:{y:"calc(25vh - 28px)"},onRow:function(t,n){return{onClick:function(t){return e.onRecoverHistory(n)}}}})))}var j=n(171),S=n(54),C=n(2),k=n.n(C);function R(e){var t=e.columns,n=e.scroll,o=Object(a.useState)(0),i=Object(u.a)(o,2),l=i[0],s=i[1],d=t.filter((function(e){return!e.width})).length,f=t.map((function(e){return e.width?e:Object(c.a)(Object(c.a)({},e),{},{width:Math.max(Math.floor(l/d),100)})})),m=Object(a.useRef)(),h=Object(a.useState)((function(){var e={};return Object.defineProperty(e,"scrollLeft",{get:function(){return null},set:function(e){m.current&&m.current.scrollTo({scrollLeft:e})}}),e})),g=Object(u.a)(h,1)[0],b=function(){m.current.resetAfterIndices({columnIndex:0,shouldForceUpdate:!1})};Object(a.useEffect)((function(){return b}),[l,e.columns]);return r.a.createElement(S.a,{onResize:function(e){var t=e.width;s(t)}},r.a.createElement(O.a,Object.assign({},e,{size:"small",className:"virtual-table",columns:f,pagination:!1,components:{body:function(e,t){var a=t.scrollbarSize,o=t.ref,i=t.onScroll;return o.current=g,r.a.createElement(j.a,{ref:m,className:"virtual-grid",columnCount:f.length,columnWidth:function(e){var t=f[e].width;return e===f.length-1?t-a-1:t},height:n.y,rowCount:e.length,rowHeight:function(){return 20},width:l,onScroll:function(e){var t=e.scrollLeft;i({scrollLeft:t})}},(function(t){var n=t.columnIndex,a=t.rowIndex,o=t.style;return r.a.createElement("div",{className:k()("virtual-table-cell",{"virtual-table-cell-last":n===f.length-1}),style:o,title:e[a][f[n].dataIndex]},e[a][f[n].dataIndex])}))}}})))}function I(e){return r.a.createElement(d.a,{style:{height:"40px",lineHeight:"40px"}},r.a.createElement(f.a,{span:8},"Current Database:",r.a.createElement("span",{style:{marginLeft:20,color:"#1890ff",fontWeight:"bold"}},e.curDatabase)))}var N=n(164),z=n.n(N).a.create(),H=function(e){return z.post("/api/runsql",{command:e})},_=function(e){return JSON.parse(JSON.stringify(e).toLowerCase())};function q(){var e=Object(a.useState)([]),t=Object(u.a)(e,2),n=t[0],o=t[1],i=Object(a.useState)([]),m=Object(u.a)(i,2),h=m[0],b=m[1],p=Object(a.useState)([]),v=Object(u.a)(p,2),x=v[0],O=v[1],y=Object(a.useState)(0),j=Object(u.a)(y,2),S=j[0],C=j[1],k=Object(a.useState)(!1),N=Object(u.a)(k,2),q=N[0],L=N[1],T=Object(a.useState)("(loading...)"),$=Object(u.a)(T,2),A=$[0],M=$[1],J=Object(a.useState)(""),B=Object(u.a)(J,2),D=B[0],Q=B[1],W=Object(a.useState)([]),P=Object(u.a)(W,2),F=P[0],Z=P[1];Object(a.useEffect)((function(){z.get("/api/curdb").then((function(e){var t=e.data;""!==t.res?M(_(t.res)):M("(No database found!)")})).catch((function(e){return console.log(e)})),b(["No result."].map((function(e){return{title:e,dataIndex:e}}))),C(window.innerHeight)}),[]);return r.a.createElement(g,{header:r.a.createElement(I,{curDatabase:A}),content:r.a.createElement(r.a.Fragment,null,r.a.createElement(d.a,{gutter:[60,36]},r.a.createElement(f.a,{span:12},r.a.createElement(w,{annotations:F,defaultCode:D,isCodeRunning:q,onRunCode:function(e){return function(e){L(!0),console.log(e),H(e).then((function(t){L(!1),t.data.forEach((function(t){var n=t.data,a=t.columns,r=t.status,i=t.message,c=t.totalTime,u=t.time,s=t.curDatabase;M(_(s)),O(_(n)),b((0===a.length?["No result."]:a).map((function(e){return{title:_(e),dataIndex:_(e)}}))),o((function(t){return[].concat(Object(l.a)(t),[{key:t.length,status:r,message:i,totalTime:c+"ms",time:u,code:e}])})),Q(e)}));var n=t.data.map((function(e){return e.error})).filter((function(e){return null!==e}));n.length?Z(n.map((function(e){return Object(c.a)(Object(c.a)({},e),{},{row:e.row-1})}))):Z([])})).catch((function(e){return console.log(e)}))}(e)},onRunSelectedCode:function(e,t,n){return function(e,t,n){L(!0),console.log(e),H(e).then((function(e){L(!1),e.data.forEach((function(e){var t=e.data,a=e.columns,r=e.status,i=e.message,c=e.totalTime,u=e.time,s=e.curDatabase;M(_(s)),O(_(t)),b((0===a.length?["No result."]:a).map((function(e){return{title:_(e),dataIndex:_(e)}}))),o((function(e){return[].concat(Object(l.a)(e),[{key:e.length,status:r,message:i,totalTime:c+"ms",time:u,code:n}])})),Q(n)}));var a=e.data.map((function(e){return e.error})).filter((function(e){return null!==e}));a.length?Z(a.map((function(e){return Object(c.a)(Object(c.a)({},e),{},{row:e.row-1+t})}))):Z([])})).catch((function(e){return console.log(e)}))}(e,t,n)}})),r.a.createElement(f.a,{span:12},r.a.createElement(E,{onClearHistory:function(){return o([])},onRecoverHistory:function(e){var t;null===(t=window.getSelection())||void 0===t||t.removeAllRanges();var a=n.find((function(t){return t.key===e}));Q(a.code),s.b.success("Recovered to history code in ".concat(a.time,"!"))},histories:n}))),r.a.createElement(d.a,{gutter:[60,36]},r.a.createElement(f.a,{span:24},r.a.createElement(R,{columns:h,dataSource:x,loading:q,scroll:{y:.75*S-190,x:"calc(100vw - 60px)"}}))))})}function L(e){return r.a.createElement("div",{style:{position:"relative",width:"100vw",height:"100vh",overflow:"hidden"}},e.children)}var T=function(){return r.a.createElement("div",{className:"App"},r.a.createElement(L,null,r.a.createElement(q,null)))};Boolean("localhost"===window.location.hostname||"[::1]"===window.location.hostname||window.location.hostname.match(/^127(?:\.(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)){3}$/));i.a.render(r.a.createElement(T,null),document.getElementById("root")),"serviceWorker"in navigator&&navigator.serviceWorker.ready.then((function(e){e.unregister()})).catch((function(e){console.error(e.message)}))}},[[174,1,2]]]);
//# sourceMappingURL=main.511888ee.chunk.js.map