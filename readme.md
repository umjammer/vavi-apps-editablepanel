[![Release](https://jitpack.io/v/umjammer/vavi-apps-editablePanel.svg)](https://jitpack.io/#umjammer/vavi-apps-editablePanel)
[![Java CI](https://github.com/umjammer/vavi-apps-editablePanel/actions/workflows/maven.yml/badge.svg)](https://github.com/umjammer/vavi-apps-editablePanel/actions/workflows/maven.yml)
[![CodeQL](https://github.com/umjammer/vavi-apps-editablePanel/actions/workflows/codeql-analysis.yml/badge.svg)](https://github.com/umjammer/vavi-apps-editablePanel/actions/workflows/codeql-analysis.yml)
![Java](https://img.shields.io/badge/Java-8-b07219)

# vavi-apps-editablePanel

## TODO

  * ~~Controller? を java beans にする~~
  * ~~プロパティエディタフレームの完成 add, delete~~
  * ~~ツリーノードのアイコン~~
  * ~~MainPanelとエディタの統合~~
  * ~~MainPanel のサイズを編集時にセットする~~
  * ~~MainPanelのモデルに子供を入れる(べき？？？)~~
  * ~~RubberBand フレームワーク，完成 MouthInputListener 化？~~
  * ~~RubberBand フレームワーク，GrassPanel の実装~~
  * JBeansTabbedPane，Palette#reset が効いていない
  * ~~EditablePanel，Controller は多分要らない~~
  * ~~EditablePanel，Cut,Copy&Paste は Clipboard API を使用する~~
  * EditablePanel，KeyEvent -> ImputMap
  * EditablePanel，Runtime のチューニング(リスナ，ビューフレーム削除等)
  * ~~プロパティエディタ，未選択時ブランクに~~
  * ~~プロパティエディタ，LayoutManager Customizer の実装~~
  * ~~020609 コンポーネントツリー，ノード最初の表示~~
  * ~~020608 コンポーネントツリー，ボタンの幅 (BoxLayoutの理解)~~
  * ~~editablePanel のコントローラを消してプロパティエディタへ渡す~~
  * プロパティエディタ，プロパティの反映

## `vavi.apps.editablePanel.beans` package

編集可能パネル`vavi.apps.editablePanel.EditablePanel`で使用されるコンポーネントのクラスを提供します．

まだコントローラを java beans 化してないので仕様は決定ではありません．
とりあえず新規コンポーネントを作成する際は，

 * `java.awt.Component`(なるべく `javax.swing.JComponent` のほうが望ましい)を継承したコンポーネントを作成する
 * そのコンポーネントに対するコントローラを，`vavi.apps.editablePanel.Controller`を継承して作成する
 * そのコンポーネントのプロパティエディタを，`vavi.apps.editablePanel.Controller`を `implemants` して作成する
 * `vavi/apps/editablePanel/EditablePanel.properties` に作成したコントローラを記述しておく
```
ep.palette.<font color=green>X</font>.title=<font color=blue>your palette title</font>
ep.palette.<font color=green>X</font>.<font color=red>0</font>=<font color=blue>your.package.yourComponent1</font>
ep.palette.<font color=green>X</font>.<font color=red>1</font>=<font color=blue>your.package.yourComponent2</font>
 :
```
 * <font color=green>X</font> ... あなたのコンポーネントを置くパレットのタイトル
 * <font color=red>2</font> ... コンポーネントの順番 (0から7まで)
 * <font color=blue>your.package.yourComponent#</font> ... パレットの番号 (0と1はシステムで予約なので2から)
