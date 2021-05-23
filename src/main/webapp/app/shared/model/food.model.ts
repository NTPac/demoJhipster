export interface IFood {
  id?: number;
  name?: string | null;
  catory?: string | null;
  description?: string | null;
  calo?: string | null;
  price?: string | null;
}

export const defaultValue: Readonly<IFood> = {};
