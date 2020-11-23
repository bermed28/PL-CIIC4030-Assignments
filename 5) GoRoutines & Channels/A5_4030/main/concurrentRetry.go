package main

import (
	"fmt"
	"sync"
)

// Result type to be returned
type Result struct {
	index  int
	result string
}

// ConcurrentRetry function
func ConcurrentRetry(tasks []func() (string, error), concurrent int, retry int) <-chan Result {

	var waitingGroup sync.WaitGroup

	fmt.Println("Creating job and result channels")

	result := make(chan Result, len(tasks))
	jobs := make(chan func() (string, int, error), len(tasks))

	fmt.Println("Spawning", concurrent, "number of workers to the worker pool")

	// Start worker pool
	for i := 0; i < concurrent; i++ {
		go worker(i, jobs, result, retry, &waitingGroup)
	}

	fmt.Println("Filling job queue channel")

	for i := 0; i < len(tasks); i++ {

		waitingGroup.Add(1)
		fun := tasks[i]
		ind := i
		task := func() (string, int, error) {
			str, err := fun()
			return str, ind, err
		}

		jobs <- task
	}

	fmt.Println("Closing job queue channel")

	close(jobs)

	// When all tasks are done close the result channel
	go func() {
		waitingGroup.Wait()
		close(result)
	}()

	fmt.Println("Returning the output channel")

	return result
}

func worker(ID int, jobs <-chan func() (string, int, error), result chan<- Result, retry int, wg *sync.WaitGroup) {

	fmt.Println("WORKER WITH ID", ID, "created and ready to operate...")
	for task := range jobs {

		failed := true

		for i := 0; i < retry; i++ {
			str, ind, err := task()
			fmt.Println("WORKER with ID", ID, "WORKING ON index", ind)
			if err == nil {
				fmt.Println("WORKER with id", ID, "finished task in", i+1, "attempts")
				toSend := Result{ind, str}
				defer wg.Done()
				failed = false
				result <- toSend
				break
			}
			fmt.Println("WORKER with ID", ID, " retrying task from index", ind)
		}

		if failed {
			fmt.Println("WORKER with ID", ID, "failed to finish task in ", retry, "attempts")
			defer wg.Done()
		}

	}

}
