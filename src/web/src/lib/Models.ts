export class OrderBookEntry {
    size!: number;
    price!: number;
    canCancel!: boolean;
    id!:string;
}

export class OrderBookResponse {
    instrumentCode!: string
    bids!: OrderBookEntry[]
    asks!: OrderBookEntry[]
}

export class Trade {
    size!: number;
    price!: number;
    id!:string;
    timestamp!:string;
}