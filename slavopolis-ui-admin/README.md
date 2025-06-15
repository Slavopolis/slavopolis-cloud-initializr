# React + TypeScript + Vite

这个模板提供了一个最小化的设置，使 React 能够在 Vite 中使用 HMR 和一些 ESLint 规则。

目前，有两个官方插件可用：

- [@vitejs/plugin-react](https://github.com/vitejs/vite-plugin-react/blob/main/packages/plugin-react/README.md) 使用 [Babel](https://babeljs.io/) 进行快速刷新
- [@vitejs/plugin-react-swc](https://github.com/vitejs/vite-plugin-react-swc) 使用 [SWC](https://swc.rs/) 进行快速刷新

## 扩展 ESLint 配置

如果您正在开发生产应用程序，我们建议更新配置以启用类型感知的 lint 规则：

- 配置顶级 `parserOptions` 属性如下：

```js
   parserOptions: {
    ecmaVersion: 'latest',
    sourceType: 'module',
    project: ['./tsconfig.json', './tsconfig.node.json'],
    tsconfigRootDir: __dirname,
   },
```

- 将 `plugin:@typescript-eslint/recommended` 替换为 `plugin:@typescript-eslint/recommended-type-checked` 或 `plugin:@typescript-eslint/strict-type-checked`
- 可选地添加 `plugin:@typescript-eslint/stylistic-type-checked`
- 安装 [eslint-plugin-react](https://github.com/jsx-eslint/eslint-plugin-react) 并将 `plugin:react/recommended` 和 `plugin:react/jsx-runtime` 添加到 `extends` 列表中
