package dev.nelly.irc

import net.ccbluex.liquidbounce.features.command.Command
import net.ccbluex.liquidbounce.utils.misc.StringUtils
import java.io.*
import java.net.Socket

class IRC : Command("irc", emptyArray()) {
    private val socket: Socket = Socket("localhost", 8000)
    private val writer: PrintWriter = PrintWriter(socket.getOutputStream(), true)
    private val irccommand = ".irc"

    override fun execute(args: Array<String>) {
         fun sendCommand(command: String) {
            writer.println(command)
            chat("已发送指令：$irccommand ${StringUtils.toCompleteString(args, 1)}")

             val inputStream = socket.getInputStream()
             val bufferedReader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
             val response = bufferedReader.readLine()

             chat("收到服务器回复：$response")
        }

        if (args.size > 1) {
            chat("NellyIRCServer")
            chat("输入 '$irccommand help' 获取帮助")

            //Help
            if (args.size > 1 && StringUtils.toCompleteString(args, 1).contains("help")) {
                chat("可用命令列表:")
                chat("- register: 注册用户 || 用法 $irccommand register 用户名 密码")
                chat("- login: 用户登录 || 用法 $irccommand login 用户名 密码")
                chat("- create: 创建频道 || 用法 $irccommand create 频道名称 管理员名称")
                chat("- join: 加入频道 || 用法 $irccommand join 频道名称")
                chat("- leave: 离开频道 || 用法 $irccommand leave")
                chat("- send: 发送消息 || 用法 $irccommand send 频道名称 需要发送的消息")
                chat("- list: 查看频道成员列表 || 用法 $irccommand list 频道名称")
                chat("- ban: 封禁用户 || 用法 $irccommand ban 频道名称 用户名称")
                chat("- unban: 解封用户 || 用法 $irccommand unban 频道名称 用户名称")
                chat("- logout: 用户登出 || 用法 $irccommand logout")
            }

            //Register
            if (args.size > 3 && StringUtils.toCompleteString(args, 1).contains("register")) {
                val username = StringUtils.toCompleteString(args, 2)
                val password = StringUtils.toCompleteString(args, 3)
                sendCommand("REGISTER $username $password")
            }

            //Login
            if (args.size > 3 && StringUtils.toCompleteString(args, 1).contains("login")) {
                val username = StringUtils.toCompleteString(args, 2)
                val password = StringUtils.toCompleteString(args, 3)
                sendCommand("LOGIN $username $password")
            }

            //Create
            if (args.size > 3 && StringUtils.toCompleteString(args, 1).contains("create")) {
                val channel = StringUtils.toCompleteString(args, 2)
                val admin = StringUtils.toCompleteString(args, 3)
                sendCommand("CREATE $channel $admin")
            }

            //Join
            if (args.size > 2 && StringUtils.toCompleteString(args, 1).contains("join")) {
                val channel = StringUtils.toCompleteString(args, 2)
                sendCommand("CREATE $channel")
            }

            //Send
            if (args.size > 3 && StringUtils.toCompleteString(args, 1).contains("send")) {
                val channel = StringUtils.toCompleteString(args, 2)
                val message = StringUtils.toCompleteString(args, 3)
                sendCommand("SEND $channel $message")
            }

            //List
            if (args.size > 1 && StringUtils.toCompleteString(args, 1).contains("list")) {
                val channel = StringUtils.toCompleteString(args, 2)
                sendCommand("LIST $channel")
            }

            //Ban
            if (args.size > 3 && StringUtils.toCompleteString(args, 1).contains("ban")) {
                val channel = StringUtils.toCompleteString(args, 2)
                val user = StringUtils.toCompleteString(args, 3)
                sendCommand("BAN $channel $user")
            }

            //UnBan
            if (args.size > 3 && StringUtils.toCompleteString(args, 1).contains("unban")) {
                val channel = StringUtils.toCompleteString(args, 2)
                val user = StringUtils.toCompleteString(args, 3)
                sendCommand("UNBAN $channel $user")
            }

            //LogOut
            if (args.size > 1 && StringUtils.toCompleteString(args, 1).contains("logout")) {
                sendCommand("LOGOUT")
            }

            //Leave
            if (args.size > 1 && StringUtils.toCompleteString(args, 1).contains("leave")) {
                socket.close()
            }
        }
    }
}
