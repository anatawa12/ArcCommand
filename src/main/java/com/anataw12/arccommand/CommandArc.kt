package com.anataw12.arccommand

import net.minecraft.block.BlockColored
import net.minecraft.block.state.IBlockState
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.command.WrongUsageException
import net.minecraft.init.Blocks
import net.minecraft.item.EnumDyeColor
import net.minecraft.server.MinecraftServer
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import kotlin.math.*


@Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
object CommandArc : CommandBase() {
    override fun getName(): String = "arc"

    @Suppress("LocalVariableName", "NonAsciiCharacters")
    override fun execute(server: MinecraftServer, sender: ICommandSender, args: Array<String>) {
        if (args.size < 3)
            throw WrongUsageException(getUsage(sender))
        //////

        val origin = sender.position

        val π = PI

        val isRight = when (args[0]) {
            "r" -> true
            "l" -> false
            else -> throw WrongUsageException(getUsage(sender))
        }

        val radius = parseDouble(args[1]) + 0.5
        val angle = parseDouble(args[2]) * π / 180
        val maxRail = if (4 <= args.size) parseInt(args[3], 10) else 200

        //視線方向
        var startAngle: Double = if (5 <= args.size) parseDouble(args[4]) else getCommandSenderAsPlayer(sender).rotationYaw.toDouble()
        startAngle = clampDegree(startAngle + 90)

        val start = when (startAngle) {
            in 337.5 .. 360.0,
            in 000.0 .. 022.5 -> Vec3d(origin).add(0.0, -0.5, 0.5)
            in 022.5 .. 067.5 -> Vec3d(origin).add(0.0, -0.5, 0.0)
            in 067.5 .. 112.5 -> Vec3d(origin).add(0.5, -0.5, 0.0)
            in 112.5 .. 157.5 -> Vec3d(origin).add(1.0, -0.5, 0.0)
            in 157.5 .. 202.5 -> Vec3d(origin).add(1.0, -0.5, 0.5)
            in 202.5 .. 257.5 -> Vec3d(origin).add(1.0, -0.5, 1.0)
            in 257.5 .. 292.5 -> Vec3d(origin).add(0.5, -0.5, 1.0)
            in 292.5 .. 337.5 -> Vec3d(origin).add(0.0, -0.5, 1.0)
            else -> error("invalid: $startAngle")
        }


        Calculator(
                start = start,
                isRight = isRight,
                radius = radius,
                startAngle = Math.toRadians(startAngle),
                angle = angle,
                maxRail = maxRail,
                receiver = ResultReceiverImpl(sender.entityWorld)
        ).main()
        //player.print(k);
    }

    fun clampDegree(degree: Double): Double {
        var degree = degree
        while (degree < 0)
            degree += 360
        while (360 <= degree)
            degree -= 360
        return degree
    }

    class ResultReceiverImpl(val world: World) : Calculator.ResultReceiver {
        private val lineBlock = Blocks.STONE.defaultState
        private val edgeBlock = Blocks.WOOL.defaultState.withProperty(BlockColored.COLOR, EnumDyeColor.RED)
        private val controlBlock = Blocks.WOOL.defaultState.withProperty(BlockColored.COLOR, EnumDyeColor.BLUE)

        override fun line(at: Vec3d) {
            world.setBlockStateIfAir(
                    BlockPos(at.x, at.y, at.z),
                    lineBlock
            )
        }

        override fun edge(at: Vec3d) {
            println("at: $at")
            val pos = BlockPos(at.x, at.y, at.z)
            val ltOne = at.subtract(Vec3d(pos))
            world.setBlockState(pos, edgeBlock)
            println("ltOne: $ltOne")

            /*
             * ┌─┬───┬─┐
             * │　│　　　│　│
             * ├─┼　　　┼─┤
             * │　　＼　／　　│
             * │　　　□　　　│
             * │　　／　＼　　│
             * ├─┼　　　┼─┤
             * │　│　　　│　│
             * └─┴───┴─┘
             */
            // 真ん中の四角
            @Suppress("ConstantConditionIf")
            if (ltOne.x in 0.375 .. 0.625 && ltOne.z in 0.375 .. 0.625) {
                // 一つだけ
            }
            // 角の四角
            else if (ltOne.x < 0.25 && ltOne.z < 0.25) {
                world.setBlockState(pos.add(-1, 0, +0), edgeBlock)
                world.setBlockState(pos.add(+0, 0, -1), edgeBlock)
                world.setBlockState(pos.add(-1, 0, -1), edgeBlock)
            } else if (ltOne.x < 0.25 && ltOne.z > 0.75) {
                world.setBlockState(pos.add(-1, 0, +0), edgeBlock)
                world.setBlockState(pos.add(+0, 0, +1), edgeBlock)
                world.setBlockState(pos.add(-1, 0, +1), edgeBlock)
            } else if (ltOne.x > 0.75 && ltOne.z < 0.25) {
                world.setBlockState(pos.add(+1, 0, +0), edgeBlock)
                world.setBlockState(pos.add(+0, 0, -1), edgeBlock)
                world.setBlockState(pos.add(+1, 0, -1), edgeBlock)
            } else if (ltOne.x > 0.75 && ltOne.z > 0.75) {
                world.setBlockState(pos.add(+1, 0, +0), edgeBlock)
                world.setBlockState(pos.add(+0, 0, +1), edgeBlock)
                world.setBlockState(pos.add(+1, 0, +1), edgeBlock)
            }
            // 斜めなど
            else if (ltOne.x < ltOne.z && ltOne.x < 1 - ltOne.z) {
                world.setBlockState(pos.add(-1, 0, +0), edgeBlock)
            } else if (ltOne.x > ltOne.z && ltOne.x < 1 - ltOne.z) {
                world.setBlockState(pos.add(+0, 0, -1), edgeBlock)
            } else if (ltOne.x < ltOne.z && ltOne.x > 1 - ltOne.z) {
                world.setBlockState(pos.add(+0, 0, +1), edgeBlock)
            } else if (ltOne.x > ltOne.z && ltOne.x > 1 - ltOne.z) {
                world.setBlockState(pos.add(+1, 0, +0), edgeBlock)
            }
            Unit
        }

        override fun controlPoint(at: Vec3d) {
            println("at: $at")
            /*
             * ┌─┬───┬─┐
             * │　│　　　│　│
             * ├─┼───┼─┤
             * │　│　　　│　│
             * │　│　　　│　│
             * │　│　　　│　│
             * ├─┼───┼─┤
             * │　│　　　│　│
             * └─┴───┴─┘
             */
            val pos = BlockPos(at.x, at.y, at.z)
            val ltOne = at.subtract(Vec3d(pos))
            world.setBlockState(pos, controlBlock)


            // 角の四角
            if (false) {
            } else if (ltOne.x < 0.25 && ltOne.z < 0.25) {
                world.setBlockState(pos.add(-1, 0, +0), controlBlock)
                world.setBlockState(pos.add(+0, 0, -1), controlBlock)
                world.setBlockState(pos.add(-1, 0, -1), controlBlock)
            } else if (ltOne.x < 0.25 && ltOne.z > 0.75) {
                world.setBlockState(pos.add(-1, 0, +0), controlBlock)
                world.setBlockState(pos.add(+0, 0, +1), controlBlock)
                world.setBlockState(pos.add(-1, 0, +1), controlBlock)
            } else if (ltOne.x > 0.75 && ltOne.z < 0.25) {
                world.setBlockState(pos.add(+1, 0, +0), controlBlock)
                world.setBlockState(pos.add(+0, 0, -1), controlBlock)
                world.setBlockState(pos.add(+1, 0, -1), controlBlock)
            } else if (ltOne.x > 0.75 && ltOne.z > 0.75) {
                world.setBlockState(pos.add(+1, 0, +0), controlBlock)
                world.setBlockState(pos.add(+0, 0, +1), controlBlock)
                world.setBlockState(pos.add(+1, 0, +1), controlBlock)

            } else if (ltOne.x < 0.25) {
                world.setBlockState(pos.add(-1, 0, +0), controlBlock)
            } else if (ltOne.x > 0.75) {
                world.setBlockState(pos.add(+1, 0, +0), controlBlock)
            } else if (ltOne.z > 0.75) {
                world.setBlockState(pos.add(+0, 0, +1), controlBlock)
            } else if (ltOne.z < 0.25) {
                world.setBlockState(pos.add(+0, 0, -1), controlBlock)
            }

            Unit
        }

    }

    override fun getUsage(sender: ICommandSender): String = "commands.arc.usage"

    override fun getRequiredPermissionLevel(): Int = 0
}

class Calculator(
        val start: Vec3d,
        val isRight: Boolean,
        val radius: Double,
        /** 視線方向 radian */
        var startAngle: Double,
        /** カーブの角度 radian */
        val angle: Double,
        /** max rail length */
        val maxRail: Int,
        val receiver: ResultReceiver
) {
    fun main () {
        val center = calculateCenter(start, startAngle, radius, isRight)
        val arcLength = calculateArcLength(radius, angle)
        val partitions = ceil(arcLength / maxRail).toInt()
        val partitionAngle = angle / partitions
        val handleLength = tan(partitionAngle / 4) * 4 / 3 * radius
        val arcStartAngle = if (isRight) startAngle - PI/2 else startAngle + PI/2

        //孤
        val oneMeter = if (isRight) angle / arcLength.toInt() else -angle / arcLength.toInt()
        repeat(arcLength.toInt()) {
            val angle = arcStartAngle + oneMeter * it
            val point = calculatePointAt(center, radius, angle)
            receiver.line(point)
        }

        val increment = if (isRight) +partitionAngle else -partitionAngle

        repeat(partitions + 1) {
            val angle = arcStartAngle + increment * it
            val point = calculatePointAt(center, radius, angle)
            receiver.edge(point)
            val control1 = calculatePointAt(point, handleLength, startAngle + increment * it)
            val control2 = calculatePointAt(point, handleLength, startAngle + increment * it + PI)
            receiver.controlPoint(control1)
            receiver.controlPoint(control2)
        }
    }

    private fun clampAngle(angle: Double): Double {
        var angle = angle
        while (angle < 0)
            angle += PI * 2
        while (PI * 2 <= angle)
            angle -= PI * 2
        return angle
    }

    private fun calculateArcLength(radius: Double, angle: Double): Double {
        /*
         *                    angle
         * 2 * radius * PI * --------
         *                    2 * PI
         */
        return radius * angle
    }

    private fun calculateCenter(start: Vec3d, startAngle: Double, radius: Double, right: Boolean): Vec3d {
        val sign = if (right) 1 else -1

        return calculatePointAt(start, radius, startAngle + sign * (PI / 2))
    }

    private fun calculatePointAt(center: Vec3d, radius: Double, angle: Double): Vec3d {
        return Vec3d(
                center.x + radius * cos(angle),
                center.y,
                center.z + radius * sin(angle)
        )
    }


    
    interface ResultReceiver {
        fun line(at: Vec3d)
        fun edge(at: Vec3d)
        fun controlPoint(at: Vec3d)
    }
}

private fun World.setBlockStateIfAir(pos: BlockPos, state: IBlockState) {
    if (isAirBlock(pos))
        setBlockState(pos, state)
}
