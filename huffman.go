package main

import (
	"container/heap"
	"fmt"
)

type Item struct {
	value    rune
	priority int
	left     *Item
	right    *Item
}

type PriorityQueue []*Item

func (pq PriorityQueue) Len() int { return len(pq) }

func (pq PriorityQueue) Less(i, j int) bool {
	return pq[i].priority < pq[j].priority
}

func (pq PriorityQueue) Swap(i, j int) {
	pq[i], pq[j] = pq[j], pq[i]
}

func (pq *PriorityQueue) Push(x any) {
	item := x.(*Item)
	*pq = append(*pq, item)
}

func (pq *PriorityQueue) Pop() any {
	old := *pq
	n := len(old)
	item := old[n-1]
	old[n-1] = nil
	*pq = old[0 : n-1]
	return item
}

func generate_frequency_map(data string) map[rune]int {
	counter := make(map[rune]int)
	for _, character := range data {
		counter[character]++
	}
	return counter
}

func generate_huffman_tree(pq PriorityQueue) *Item {
	for pq.Len() > 1 {
		left := heap.Pop(&pq).(*Item)
		right := heap.Pop(&pq).(*Item)
		newNode := &Item{
			priority: left.priority + right.priority,
			left:     left,
			right:    right,
		}
		heap.Push(&pq, newNode)
	}
	return heap.Pop(&pq).(*Item)
}

func generate_code(root *Item, prefix string, code map[rune]string) {
	if root == nil {
		return
	}

	if root.left == nil && root.right == nil {
		code[root.value] = prefix
		return
	}

	generate_code(root.left, prefix+"0", code)
	generate_code(root.right, prefix+"1", code)

}

func encode_sequence(sequence string, code map[rune]string) string {
	encoded_sequence := ""
	for _, character := range sequence {
		encoded_sequence += code[character]
	}
	return encoded_sequence
}

func main() {
	sequence := "AAABBBACCDA"
	counter := generate_frequency_map(sequence)
	pq := make(PriorityQueue, len(counter))

	i := 0
	for character, frequency := range counter {
		pq[i] = &Item{
			value:    character,
			priority: frequency,
		}
		i++
	}
	heap.Init(&pq)

	root := generate_huffman_tree(pq)
	code := make(map[rune]string)

	generate_code(root, "", code)

	fmt.Println("Input Sequence: ", sequence)
	for character := range code {
		fmt.Printf("%c: %s\n", character, code[character])
	}

	fmt.Println("Encoded Sequence: ", encode_sequence(sequence, code))
}
