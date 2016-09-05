/* @flow */

export type Test = {
    id: string;
    name: string;
    description: string;
    url: string;
    interval: number;
    enabled?: boolean;
    selected?: boolean;
};