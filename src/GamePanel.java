import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH*SCREEN_HEIGHT)/UNIT_SIZE;
    static final int DELAY = 75;
    final int[] x = new int[GAME_UNITS];
    final int[] y = new int[GAME_UNITS];
    int bodyParts = 6;
    int applesEaten;
    int appleX;
    int appleY;
    char direction = 'R';
    char initialDirection = 'R';
    boolean running = false;
    private final JButton restartButton;
    Timer timer;
    Random random;
    GamePanel(){
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        restartButton = new JButton("Restart");
        restartButton.setFocusable(false);
        restartButton.addActionListener(e -> {
            applesEaten = 0;
            initialDirection = 'R';
            startGame();
            restartButton.setVisible(false);
        });
        add(restartButton);
        restartButton.setBounds(SCREEN_WIDTH / 2 - 50, SCREEN_HEIGHT / 2 - 20, 100, 40);
        restartButton.setVisible(false);
        startGame();
    }
    public void startGame() {
        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();

        int initialX = SCREEN_WIDTH / 2;
        int initialY = SCREEN_HEIGHT / 2;
        for (int i = 0; i < bodyParts; i++) {
            x[i] = initialX - i * UNIT_SIZE;
            y[i] = initialY;
        }
        direction = initialDirection;
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g){
        if (running){
//            for (int i = 0; i < SCREEN_HEIGHT/UNIT_SIZE; i++) {
//                g.drawLine(0,i*UNIT_SIZE,SCREEN_WIDTH,i*UNIT_SIZE);
//            }
            g.setColor(Color.red);
            g.fillOval(appleX, appleY, UNIT_SIZE,UNIT_SIZE);

            for (int i = 0; i < bodyParts; i++) {
                if (i == 0){
                    g.setColor(Color.green);
                    g.fillRect(x[i],y[i],UNIT_SIZE, UNIT_SIZE);
                }
                else {
                    g.setColor(new Color(45,180,0));
                    g.fillRect(x[i],y[i],UNIT_SIZE, UNIT_SIZE);
                }
            }
        }
        else {
            gameOver(g);
        }
        g.setColor(Color.red);
        g.setFont(new Font("New Times Roman",Font.BOLD,34));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Score: "+applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: "+applesEaten))/2, g.getFont().getSize());
    }

    public void newApple(){
        appleX = random.nextInt(SCREEN_WIDTH/UNIT_SIZE)*UNIT_SIZE;
        appleY = random.nextInt(SCREEN_HEIGHT/UNIT_SIZE)*UNIT_SIZE;
    }

    public void move(){
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i-1];
            y[i] = y[i-1];
        }
        switch (direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
            break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }

    }

    public void checkApple(){
        if ((x[0] == appleX) && (y[0] == appleY)){
            bodyParts++;
            applesEaten++;
            newApple();
        }

    }

    public void checkCollisions(){
        // checks if head hits body
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])){
                running = false;
            }
        }
        // checks if head hits left border
        if (x[0] < 0){
            running = false;
        }
        // check if head hits right border
        if (x[0] > SCREEN_WIDTH){
            running = false;
        }
        // check if head hits top border
        if (y[0] < 0){
            running = false;
        }
        // check if head hits bottom border
        if (y[0] > SCREEN_HEIGHT){
            running = false;
        }
        if (!running){
            timer.stop();
        }
    }

    private void restartGame() {
        // Tutaj dodaj kod resetujący grę, np. ustaw węża i liczniki w początkowe wartości
        bodyParts = 6;
        applesEaten = 0;
        direction = 'R';
        running = true;
        timer.start();
        newApple();
        restartButton.setVisible(false); // Ukryj przycisk restartu po restarcie
    }

    public void gameOver(Graphics g){
        //Game Over text
        g.setColor(Color.red);
        g.setFont(new Font("New Times Roman",Font.BOLD,75));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("GameOver", (SCREEN_WIDTH - metrics1.stringWidth("GameOver"))/2,SCREEN_HEIGHT/2);

        g.setColor(Color.red);
        g.setFont(new Font("New Times Roman",Font.BOLD,34));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Score: "+applesEaten, (SCREEN_WIDTH - metrics2.stringWidth("Score: "+applesEaten))/2, g.getFont().getSize());
        restartButton.setVisible(true);

    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (running){
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent e){
            switch (e.getKeyCode()){
                case KeyEvent.VK_LEFT:
                    if (direction != 'R'){
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L'){
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D'){
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U'){
                        direction = 'D';
                    }
                    break;
            }
        }
    }
}
