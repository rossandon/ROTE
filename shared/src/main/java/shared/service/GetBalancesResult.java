package shared.service;

import java.util.HashMap;

public record GetBalancesResult(HashMap<String, Long> balances) {
}
