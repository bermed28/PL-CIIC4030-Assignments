package main

import (
	"errors"
	"fmt"
	"math/rand"
	"time"
)

func main() {

	var taskSlice []func() (string, error)

	for i := 0; i < 12; i++ {
		taskSlice = append(taskSlice, sampleTask)
	}

	o := ConcurrentRetry(taskSlice, 10, 9)
	o2 := ConcurrentRetry(taskSlice, 15, 1)

	for ch := range o {
		fmt.Println(ch)
		fmt.Println("WAITING ON NEXT OUTPUT...")
	}

	for ch2 := range o2 {
		fmt.Println(ch2)
		fmt.Println("WAITING ON NEXT OUTPUT...")
	}

	fmt.Println("Finished ...")

}

func sampleTask() (string, error) {

	// Costly random computation simulation
	rand.Seed(time.Now().UnixNano())
	n := rand.Intn(10) //  0 <= n <= 10
	time.Sleep(3 * time.Second + time.Duration(n) * time.Second)

	// If there is a 60% chance of failing some task
	if n <= 6 {
		return "Done", errors.New("Failed Task")
	}

	return "Done", nil

}
