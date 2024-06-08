package shared.service.results;

import java.math.BigDecimal;
import java.util.HashMap;

public record GetBalancesResult(HashMap<String, BigDecimal> balances) {
}
