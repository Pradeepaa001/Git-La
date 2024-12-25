/*
import scala.collection.mutable.PriorityQueue
import scala.collection.mutable

case class Node(char: Option[Char], freq: Int, left: Option[Node] = None, right: Option[Node] = None) extends Ordered[Node]{
    def compare(that: Node): Int = this.freq - that.freq
}
object huffman{

    def buildTree(frequency: Map[Char, Int]): Node = {
        val pQueue = PriorityQueue[Node]()(Ordering.by(-_.freq))
        for ((char, freq) <- frequency){
            pQueue.enqueue(Node(Some(char), freq))
        }

        while pQueue.size > 1 do
            val left = pQueue.dequeue()
            val right = pQueue.dequeue()
            val merged = Node(None, left.freq + right.freq, Some(left), Some(right))
            pQueue.enqueue(merged)
        pQueue.dequeue()
    }

    def generateCode(node: Node, prefix:String = "", code: mutable.Map[Char, String] = mutable.Map()): mutable.Map[Char, String]= {
        node.char match {
            case Some(char) => code(char) = prefix
            case None =>
                node.left.foreach(generateCode(_, prefix + "0", code))
                node.right.foreach(generateCode(_, prefix + "1", code))
        }
        code
    }
    @main def encode(args: String*): Unit ={
        if args.isEmpty then
            return 
        
        val input_string = args.mkString(" ")
        val frequency = input_string.groupMapReduce(identity)(_ => 1)(_ + _)
        //println(frequency)
        val root = buildTree(frequency)
        //println(root)
        val code =  generateCode(root)
        println("Map: " + code)
        val encoded_str = input_string.flatMap(code.get).mkString
        println("Encoded String: " + encoded_str)
    }
}
*/