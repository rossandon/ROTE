package shared.service.results;

import java.util.HashMap;

public record GetBalancesResult(HashMap<String, Long> balances) {
}
