<script>
  export default {
    name: "MenuItem",
    functional: true,
    props: {
      icon: {
        type: String,
        default: ""
      },
      title: {
        type: String,
        default: ""
      }
    },
    // render (h, context) {
    //   const { icon, title } = context.props
    //   const vnodes = []
    //
    //   if (icon) {
    //     vnodes.push(<svg-icon icon-class={icon} width="1.5em" height="1.5em"/>)
    //   }
    //
    //   if (title) {
    //     vnodes.push(<span slot='title'>{(title)}</span>)
    //   }
    //   return vnodes
    // }
    render: function (createElement, context) {
      const vnodes = []
      if (context.props.icon) {
        const svgIcon = createElement("svg-icon", {
          props: {
            "icon-class": context.props.icon,
            "width": "1.5em",
            "height": "1.5em"
          }
        })
        vnodes.push(svgIcon)
      }
      if (context.props.title) {
        const span = createElement("span", {
          slot: "title",
          domProps: {
            innerHTML: context.props.title
          }
        })
        vnodes.push(span)
      }
      return createElement("div", null, vnodes)
    }
  }
</script>
