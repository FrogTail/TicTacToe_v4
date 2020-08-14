import java.util.Scanner;

public class TicTacToe_v4
{
    private static final int EMPTY = -1;
    private static final int ZERO = 0;
    private static final int CROSS = 1;
    private static final String EMPTY_SIGN = " - ";
    private static final String ZERO_SIGN = " O ";
    private static final String CROSS_SIGN = " X ";

    // Возможно добавлю несколько уровней АИ, поэтому стринг, а не булеан
    private static final String HUMAN = "HUMAN";
    private static final String COMPUTER = "COMPUTER";
    private static final String DRAW = "DRAW";

    private static final int[][] DIRECTIONS = {{0,-1},{1,-1},{1,0},{1,1},
            {0,1},{-1,1,},{-1,0},{-1,-1}};

    // Основание для расчета рейтинга тайла
    private static final int FOUNDATION = 8;

    private static Scanner SCANNER = new Scanner(System.in);

    // Опции игры.
    // Пока меняем тут, но потом можно добавить методы для настройки опций игроком
    private static final int[] playersSigns = {CROSS,ZERO};
    private static final String[] playersOptions = {HUMAN,COMPUTER};
    private static final String[] playersNames = {"Игрок 1", "Игрок 2"};
    private static final int fieldSizeX = 9;
    private static final int fieldSizeY = 9;
    private static final int winCondition = 5;


    public static void main (String[] args)
    {
        newGame();
    }


    // Новая игра.
    // Вынесена в отдельный метод, тк будет еще метод изменения настроек.
    private static void newGame ()
    {
        int[][] field = newField(fieldSizeY,fieldSizeX);

        System.out.println("\n Условие победы - " +winCondition +" в ряд.\n");

        printField(field);

        int currentPlayer = 0;

        while (true)
        {

            if (playersOptions[currentPlayer].equals(HUMAN))
                humansTurn(field, currentPlayer);
            else
                computersTurn(field, currentPlayer);

            System.out.println();
            printField(field);

            if (checkWin(field,playersSigns[currentPlayer]))
            {
                System.out.println("Победил " +playersNames[currentPlayer] +"!");
                break;
            }
            else if (checkDraw(field))
            {
                System.out.println("НИЧЬЯ!");
                break;
            }

            currentPlayer ++;
            if (currentPlayer>playersSigns.length-1)
                currentPlayer=0;
        }

    }


    // Печатаем карту рейтингов (служебная функция)
    public static void printRate (int[][] rates)
    {
        for(int y=0; y<rates.length; y++)
        {
            for(int x=0; x<rates[y].length; x++)
            {
                System.out.printf("%5d",rates[y][x]);
            }
            System.out.println();
        }
    }


    // Создаем карту рейтингов пустых тайлов для выбранного знака
    private static int[][] getRates (int[][] field, int sign)
    {
        int[][] rates = new int[field.length][field[0].length];

        {
            for(int y=0; y<rates.length; y++)
            {
                for(int x=0; x<rates[y].length; x++)
                {
                    if (isValid(field,x,y))
                    {
                        if(isEmpty(field,x,y))
                            rates[y][x]=countTileRate(field,x,y,sign);
                    }
                }
            }
        }
        return rates;
    }


    // Считаем рейтинг тайла
    private static int countTileRate (int[][] field, int givenX, int givenY, int sign)
    {
        int x = givenX;
        int y = givenY;

        int tileRate = 0;

        for (int dir = 0; dir < 4; dir++)
        {
            for (int shift = 0; shift <= winCondition - 1; shift++)
            {
                x = givenX + DIRECTIONS[dir][0]*shift*(-1);
                y = givenY + DIRECTIONS[dir][1]*shift*(-1);

                int lineScores = countLineScores(field, x, y, dir, sign, givenX, givenY);

                if (lineScores>0)
                    tileRate += Math.pow(FOUNDATION, lineScores);
                //System.out.println("[countTileRate]: x=" + x + " y=" + y + " tileRate=" + tileRate);
            }
        }
        return tileRate;
    }


    // Считаем очки в линии
    private static int countLineScores (int[][] field, int givenX, int givenY, int dir, int sign, int virtualX, int virtualY)
    {
        int x = givenX;
        int y = givenY;

        int scores = 0;

        for (int step = 0; step < winCondition; step++)
        {
            x = givenX + DIRECTIONS[dir][0]*step;
            y = givenY + DIRECTIONS[dir][1]*step;

            if (isValid(field,x,y))
            {
                if (field[y][x] == sign || (x == virtualX && y == virtualY))
                    scores++;
                else if (field[y][x] != EMPTY)
                {
                    scores=0;
                    break;
                }
            }
            else
            {
                scores = 0;
                break;
            }
        }

        //System.out.println("[countLineScores]: X=" +givenX +" Y=" +givenY +" DIR=" +dir +" SIGN=" +sign +" vX=" +virtualX +" vY=" +virtualY +" Scores=" +scores);
        return scores;
    }


    // Проверяем все поле на победу конкретного знака
    private static boolean checkWin (int[][]field, int sign)
    {
        for (int y=0; y<field.length; y++)
        {
            for (int x=0; x<field[y].length; x++)
            {
                if (field[y][x]==sign)
                {
                    for (int dir = 0; dir <4; dir ++)
                    {
                        if (countLineScores(field,x,y,dir,sign,-1,-1)==winCondition)
                            return true;
                    }
                }
            }
        }
        return false;
    }


    // Проверка на ничью,
    // правда примитивная, потом допилю поиск "доступных линий"
    private static boolean checkDraw (int[][] field)
    {
        for (int y=0; y<field.length; y++)
        {
            for (int x=0; x<field[y].length; x++)
            {
                if (isEmpty(field, x, y))
                    return false;
            }
        }

        return true;
    }


    // Ход игрока
    private static void humansTurn (int[][] field, int currentPlayer)
    {
        int sign = playersSigns[currentPlayer];

        System.out.print("\nВаш ход, укажите координаты Х и Y через пробел:");

        int x;
        int y;

        do
        {
            x = SCANNER.nextInt();
            y = SCANNER.nextInt();

            if (isEmpty(field,x-1,y-1))
                break;

            System.out.print("Введенные координаты некорректны. Пожалуйста, введите еще раз:");
        }while(true);

        field[y-1][x-1] = sign;
    }


    // Ход компьютера
    private static void computersTurn (int[][] field, int currentPlayer)
    {
        int mySign = playersSigns[currentPlayer];

        int enemySign = ZERO;

        if (mySign==ZERO)
            enemySign = CROSS;

        // формируем карту рейтингов тайлов
        int[][] myRates = getRates(field,mySign);
        int[][] enemyRates = getRates(field,enemySign);

        // Просту суммируем наш рейтинг и рейтинг противника
        // Это не слишком корректно, но текущую задачу решает
        int[][] totalRate = new int[myRates.length][myRates[0].length];
        int maxRate = -1;
        int xOfMaxRate=0;
        int yOfMaxRate=0;

        for(int y=0; y<totalRate.length; y++)
        {
            for(int x=0; x<totalRate[y].length; x++)
            {
                totalRate[y][x]=myRates[y][x]+enemyRates[y][x];
                if (totalRate[y][x]>maxRate)
                {
                    maxRate=totalRate[y][x];
                    xOfMaxRate=x;
                    yOfMaxRate=y;
                }
            }
        }

        //System.out.println();
        //printRate(totalRate);

        field[yOfMaxRate][xOfMaxRate]=mySign;
    }


    // Проверяем клетку на доступность
    private static boolean isEmpty (int[][] field, int x, int y)
    {
        return  (isValid (field,x,y) && field[y][x]==EMPTY);
    }


    // Проверяем координаты на валидность
    private static boolean isValid (int[][] field, int x, int y)
    {
        return (y>=0 && y<field.length && x>=0 && x<field[0].length );
    }


    // Отображаем поле
    private static void printField (int[][] field)
    {
        // Не стал заморачиваться с универсальным алгоритмом, выравнивание почти ручное
        String vHeader = "   ";

        for (int x=0; x<field[0].length; x++)
        {
            if (x+1<10)
                vHeader += " " + (x+1) +" ";
            else
                vHeader += " " + (x+1);
        }

        System.out.println(vHeader);

        for (int y=0; y<field.length; y++)
        {
            System.out.printf("%2d ", y+1);

            for (int x=0; x<field[y].length; x++)
            {
                String currentSign = "";

                switch (field[y][x])
                {
                    case (CROSS):
                        currentSign = CROSS_SIGN;
                        break;
                    case (ZERO):
                        currentSign = ZERO_SIGN;
                        break;
                    default:
                        currentSign = EMPTY_SIGN;
                        break;
                }

                System.out.print(currentSign);
            }

            System.out.printf(" %-2d", y+1);
            System.out.println();
        }

        System.out.println(vHeader);
    }


    // Создаем поле и заполняем его пустыми значениями
    private static int[][] newField (int sizeX, int sizeY)
    {
        int[][] field = new int[sizeY][sizeX];

        for (int y=0; y<field.length; y++)
        {
            for (int x=0; x<field[y].length; x++)
            {
                field[y][x]=EMPTY;
            }
        }
        return field;
    }

}
