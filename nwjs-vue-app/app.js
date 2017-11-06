Vue.use(VueMaterial)

var App = new Vue({
  el: '#app'
})

Vue.material.registerTheme({
default: {
  primary: 'blue',
  accent: 'red'
},
green: {
  primary: 'green',
  accent: 'pink'
},
orange: {
  primary: 'orange',
  accent: 'green'
},
})