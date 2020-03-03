# arc command 

adds `/arc` command.
`/arc` コマンドを追加します

# 使い方 How to use

`/arc <r|l> [半径] [角度] [最大レール長 = 200] [開始角度 = 視線の向き]`
`/arc <l|r> [radius] [angle] [rail length = 200] [start-angle = looking]`

`<r|l>`\
:  *必須* 右方向に曲げるか左方向に曲げるか\
:  *required* arc to left or right

`[半径]` `[radius]`\
:  *必須* 弧の半径\
:  *required* the radius of the arc

`[角度]` `[angle]`\
:  *必須* レールを敷く角度\
:  *required* the angle of the arc

`[最大レール長]`\
:  *任意* 一つのレールの長さ デフォルト: 200\
:  *optional* length of the one rail. default: 200

`[開始角度]`\
:  *任意* レールを敷き始める角度 デフォルト: 視線の向き\
:  *optional* the angle of start of the arc. default: the angle you're looking

# 設置されるブロック

石 stone\
:  円弧を表します\
:  line on arc

赤羊毛\
:  マーカーを置く場所を示します。1/2/4つの中心でつながるように2つのマーカーを置くとときれいな円になります\
:  the place to put marker. if you put two markers at the center of 1/2/4 blocks, the arc will be beautiful.

青羊毛\
:  制御点/ハンドルを置く場所を示します。1/2/4つの中心に置くときれいな円になります\
:  the place to put handle/control point. if you put it at the center of 1/2/4 blocks, the arc will be beautiful.

# 前提 dependencies

minecraft forge 1.12.2-14.23.x.x
