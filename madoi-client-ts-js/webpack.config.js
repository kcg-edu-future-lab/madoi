module.exports = {
  // development or production
  mode: 'development',
  entry: './src/madoi.ts',
  module: {
    rules: [
      {
        // 拡張子 .ts の場合
        test: /\.ts$/,
        // TypeScript をコンパイルする
        use: 'ts-loader',
      },
    ],
  },
  resolve: {
    extensions: [
      '.ts', '.js',
    ],
  },
  output: {
    library: "madoi",
    filename: 'madoi.js',
    libraryTarget: "umd"
  },
  devServer: {
    contentBase: "dist",
    watchContentBase: true,
    open: true
  }
};
