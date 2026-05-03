# Refactoring Opportunities

| Local | Nome do Cheiro | Nome da Refabricação | Nº Aluno |
|-------|---------------|---------------------|----------|
| Move:processEnemyFire|Repeated code fragments|Extract Method|122975|
| Ship::StillFloating|Deep Nesting|Extract Method|122975|
| Game (linha 123) | Redundant Initialization | Safe Delete | 123901 |
| Game (class) | Large Class | Extract Class | 123901 |
| Tasks::menu | Long Method | Extract Method | 123901 |
| Game::randomEnemyFire | Long Method | Decompose Conditional | 123901 |
| Ship (linha 248) | Suspicious Indentation | Add Braces | 123901 |
| Fleet::create             | Random Duplicated Code                 | Extract Constant / Replace with Constant | 123002 |
| Game::printBoard          | Long Method                            | Extract Method                           | 123002 |
| Game::randomEnemyFire     | Long Method                            | Extract Method                           | 123002 |
| Tasks::menu                | Long Method / Switch Statements        | Extract Method                           | 123002 |
| Move::processEnemyFire    | Duplicated Code                        | Extract Constant / Replace with Constant | 123002 |
| Move::processEnemyFire    | Long Method                            | Extract Method                           | 123002 |
| Move::printVerboseSummary (método novo) | Nested expressions / Complex condition | Decompose Conditional / Extract Method   | 123002 |

## Houve alguns Refactorings que tivémos de repetir em branches diferentes, uma vez que não haviam suficientes para cada um fazer pelo menos 5
