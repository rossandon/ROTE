export class OrderBookEntry {
    size!: number;
    price!: number;
    canCancel!: boolean;
}

export class OrderBookResponse {
    instrumentCode!: string
    bids!: OrderBookEntry[]
    asks!: OrderBookEntry[]
}