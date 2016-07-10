### KAFKA CONSUMER AT-LEAST-ONCE DELIVERY WITH FAILURES
Experimenting with kafka consumer/producer to achieve at least once delivery with failures during business logic

#### REACTIVE KAFKA COMMITTABLE SOURCE
	1. RUN
		Record experiment 10 9 failed
		Record experiment 10 1 failed
		Record experiment 10 2 processed
		Record experiment 10 4 processed
		Record experiment 10 6 failed
		Record experiment 10 8 failed
		Record experiment 10 3 processed
		Record experiment 10 10 processed
		Record experiment 10 5 failed
		Record experiment 10 7 failed
	2. RUN
		Record experiment 10 5 failed
		Record experiment 10 7 failed
	3. RUN
		Record experiment 10 5 processed
		Record experiment 10 7 processed
	RESULT:
		Some of the messages(1, 9, 6, 8) are lost. Processing some of the messages can only be achieved by restarting the consumer

#### KAFKA CONSUMER WITHOUT SEEK
	1. RUN
		Record: experiment 13 1 failed
		Record: experiment 13 2 processed
		Record: experiment 13 4 processed
		Record: experiment 13 6 failed
		Record: experiment 13 8 failed
		Record: experiment 13 3 processed
		Record: experiment 13 5 processed
		Record: experiment 13 7 failed
		Record: experiment 13 9 failed
		Record: experiment 13 10 processed
	2. RUN
		NOTHING
	RESULT:
		Some of the messages(1, 6, 8, 7, 9) are lost. Processing some of the messages could only be achieved by restarting the consumer

#### KAFKA CONSUMER WITH SEEK BACKWARDS FOR FAILED MESSAGES
	1. RUN
		Record: experiment 14 2 failed
		Record: experiment 14 4 failed
		Record: experiment 14 1 processed
		Record: experiment 14 3 processed
		Record: experiment 14 5 failed
		Record: experiment 14 7 failed
		Record: experiment 14 2 processed
		Record: experiment 14 4 processed
		Record: experiment 14 6 failed
		Record: experiment 14 8 failed
		Record: experiment 14 5 processed
		Record: experiment 14 7 processed
		Record: experiment 14 6 failed
		Record: experiment 14 9 failed
		Record: experiment 14 6 processed
		Record: experiment 14 8 processed
		Record: experiment 14 10 failed
		Record: experiment 14 9 failed
		Record: experiment 14 10 processed
		Record: experiment 14 9 processed
	RESULT:
		No messages lost. No restart required.
