declare module '*.vue' {
    import { ComponentOptions } from 'vue'
    const componentOptions: ComponentOptions
    export default componentOptions
  }
declare module '*.css' {
  const content: { [className: string]: string };
  export default content;
}