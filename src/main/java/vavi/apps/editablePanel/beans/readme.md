# `vavi.apps.editablePanel.beans`

provides component classes that are used by `vavi.apps.editablePanel.EditablePanel`.

まだコントローラを java beans 化してないので仕様は決定ではありません．
とりあえず新規コンポーネントを作成する際は，

* `java.awt.Component`(なるべく `javax.swing.JComponent` のほうが望ましい)を継承したコンポーネントを作成する
* そのコンポーネントに対するコントローラを，`vavi.apps.editablePanel.Controller`を継承して作成する
* そのコンポーネントのプロパティエディタを，`vavi.apps.editablePanel.Controller`を `implemants` して作成する
* `vavi/apps/editablePanel/EditablePanel.properties` に作成したコントローラを記述しておく
<pre>
ep.palette.<font color=green>X</font>.title=<font color=blue>your palette title</font>
ep.palette.<font color=green>X</font>.<font color=red>0</font>=<font color=blue>your.package.yourComponent1</font>
ep.palette.<font color=green>X</font>.<font color=red>1</font>=<font color=blue>your.package.yourComponent2</font>
 :
</pre>
* <font color=green>X</font> ... あなたのコンポーネントを置くパレットのタイトル
* <font color=red>2</font> ... コンポーネントの順番 (0から7まで)
* <font color=blue>your.package.yourComponent#</font> ... パレットの番号 (0と1はシステムで予約なので2から)
