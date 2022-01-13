export interface ICoffee {
  id?: number;
  name?: string | null;
  price?: number | null;
}

export class Coffee implements ICoffee {
  constructor(public id?: number, public name?: string | null, public price?: number | null) {}
}

export function getCoffeeIdentifier(coffee: ICoffee): number | undefined {
  return coffee.id;
}
