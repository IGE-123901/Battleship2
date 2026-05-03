# Refactoring Opportunities

| Local | Nome do Cheiro | Nome da Refabricação | Número do Aluno responsável |
|-------|----------------|----------------------|-----------------------------|
| Game::printBoard | Long Method | Extract Method | LE123901 |
| Game::printBoard | Deep Nesting | Extract Method | LE123901 |
| Game::randomEnemyFire | Long Method | Extract Method | LE123901 |
| Game::readEnemyFire | Long Method | Extract Method | LE123901 |
| Move::processEnemyFire | Long Method | Extract Method | LE123901 |
| Move::processEnemyFire | Overly Complex Method | Extract Method | LE123901 |
| Game::fireShots | Long Method | Extract Method | LE123901 |
| Game::printMyBoard / Game::printAlienBoard | Duplicate Code | Extract Method | LE123901 |
| Game (class) | Large Class | Extract Class | LE123901 |
| Move::processEnemyFire | Long Method | Move Method | LE123901 |
| Ship::StillFloating| Long Method (suspicious indentation) | Extract Method |122975 |
| Ship | Redundant Assignment | Inline Variable | 122975|
| Game::jsonShots | Redundant Initialization | Inline Variable | 122975|
| Tasks::menu | Busy Wait | Extract Method | 122975|
| Game::randomEnemyFire | Performance (removeAll) | Substitute Algorithm | 122975|
