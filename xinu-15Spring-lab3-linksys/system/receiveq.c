#include <xinu.h>

umsg32	receiveq(void)
{
	intmask	mask;				/* saved interrupt mask		*/
	struct	procent *prptr;		/* ptr to process' table entry	*/
	struct	procent *sender;		/* ptr to process' table entry	*/

	umsg32	msg;				/* message to return		*/
	pid32 	deq;

	mask = disable();
	prptr = &proctab[currpid];
	if (prptr->prhasmsg == FALSE || prptr->msgqCount < 0) {
		prptr->prstate = PR_RECV;
		resched();		/* block until message arrives	*/
	}

	deq = dequeue(prptr->msgq);
	sender = &proctab[deq];
	msg = sender->prmsg;		/* retrieve message		*/
	prptr->prhasmsg = FALSE;	/* reset message flag		*/

	if(nonempty(prptr->receivelist))
	{
		deq = dequeue(prptr->receivelist);
		sender = &proctab[deq];
		sender->prstate = PR_READY;
		enqueue(deq,prptr->msgq);
	}

	restore(mask);
	return msg;
}
