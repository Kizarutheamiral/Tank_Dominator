import pygame
from pygame.locals import *
import math
import random

GAME_SIZE = 900
V_MAX = 5.0
A_MAX = 0.1
X_DEST = 375.0
Y_DEST = 375.0
MOVE = True

def start_game():
    pygame.init()
    fenetre = pygame.display.set_mode((GAME_SIZE, GAME_SIZE))
    for i in range(2):
        Perso((350.0, 350.0-30*i), random.randint(20, 20), True)
    Obstacle(500, 500, 100)    
    pygame.key.set_repeat(1, 1)
    clock = pygame.time.Clock()
    continuer = True
    while continuer:        
        clock.tick(60)
        center = [0, 0]
        n = float(len(Perso.persos))
        for p in Perso.persos:
            center[0] += p.x
            center[1] += p.y
        center[0] /= n
        center[1] /= n
        global MOVE
        dest = [Perso.persos[0].xDest, Perso.persos[0].yDest]
        diff = math.sqrt((center[0] - dest[0])**2 + (center[1] - dest[1])**2)
        if diff > 50: # REMPLACER PAR LE CHIFFRE CORRESPONDANT AU NB BOIDS
            MOVE = True            
        else:
            MOVE = False

        #sorted(Perso.persos, key=lambda p: p.distDest) # ceux en avant avance avant ceux en arrière
              
        for p in Perso.persos:
            p.detectObstacle(Obstacle.obstacles[0])
            for p2 in Perso.persos:
                if p != p2:
                    p.detectObstacle(p2)
            p.moveToTarget()

        fenetre.fill((255, 255, 255))
        for p in Perso.persos: p.draw(fenetre)
        for o in Obstacle.obstacles: o.draw(fenetre)                    
        for event in pygame.event.get():
            keys = pygame.key.get_pressed()
            if event.type == QUIT:
                continuer = 0
            if event.type == KEYDOWN:
                if event.key == K_SPACE:
                    continuer = 0
        if pygame.mouse.get_pressed()[0]:
            pos = pygame.mouse.get_pos()
            for p in Perso.persos:
                if p == Perso.persos[0]:
                    p.xDest = pos[0]
                    p.yDest = pos[1]
        pygame.display.flip()
    pygame.quit()


class Perso:

    persos = []

    def __init__(self, pos, size, solid):
        self.x, self.y = pos
        self.mass = 1.0
        self.xDest = X_DEST
        self.yDest = Y_DEST
        self.size = size
        self.xspeed = 0
        self.yspeed = 0
        self.xAcc = 0
        self.yAcc = 0
        self.xHead = 1
        self.yHead = 0
        self.solid = solid
        self.color = (random.randint(1, 255), random.randint(1, 255), random.randint(1, 255))
        Perso.persos.append(self)

    def draw(self, fenetre):
        pygame.draw.circle(fenetre, self.color, (int(self.x), int(self.y)), int(self.size))
        pygame.draw.line(fenetre, (255, 255, 255), (int(self.x), int(self.y)), (int(self.x+self.size*self.xHead), int(self.y+self.size*self.yHead)), 1)

    def detectObstacle(self, o):
        toTarget = [o.x - self.x, o.y - self.y]
        dist = math.sqrt(toTarget[0]**2 + toTarget[1]**2)
        pdt = self.xHead*toTarget[0] + self.yHead*toTarget[1]
        if pdt > 0:            
            radius = o.size
            length = o.size/2.0
            if dist <= radius + self.size + length:
                if (o.x-self.x)*(self.yDest-self.y)-(o.y-self.y)*(self.xDest-self.x) > 0: # FAIT PRENDRE LE PLUS COURS CHEMIN POUR ATTEINDRE LA CIBLE
                    self.xAcc = (-toTarget[1]/dist) * (1-((dist - radius - self.size)/(length)))
                    self.yAcc = ( toTarget[0]/dist) * (1-((dist - radius - self.size)/(length)))
                    self.xspeed += self.xAcc
                    self.yspeed += self.yAcc
                else:
                    self.xAcc = -(-toTarget[1]/dist) * (1-((dist - radius - self.size)/(length)))
                    self.yAcc = -( toTarget[0]/dist) * (1-((dist - radius - self.size)/(length)))
                    self.xspeed += self.xAcc
                    self.yspeed += self.yAcc

    def moveToTarget(self):
        vTarget = [self.xDest - self.x, self.yDest - self.y]
        dist = math.sqrt(vTarget[0]**2 + vTarget[1]**2)
        if dist > V_MAX:        # V_MAX étant interprétable comme la distance max entre 2 frames de l'agent
            speed = dist/20.0    # dist / w où w est plus grand que 1 pour qu'il y ait un ralenti -> si on freine, trouver
                                # moyen pour quela vitesse ne devienne pas assez petite pour que le retournement soit quasi instantané -------------|
            speed = min(speed, V_MAX)                                                                                               #               | TROUVÉ!
            desiredVel = [speed * vTarget[0] / dist, speed * vTarget[1] / dist]                                                     #               |
            speedNorm = math.sqrt(self.xspeed**2 + self.yspeed**2)                                                                  #               v
            if speedNorm > 0 and abs((self.xspeed/speedNorm)+vTarget[0]/dist) < 0.1 and abs((self.yspeed/speedNorm)+vTarget[1]/dist) < 0.1: # QUAND OPPOSÉ, PREND LA PERP
                self.xAcc = -desiredVel[1]                                  #    ^--- EN FAIRE UNE CONSTANTE...
                self.yAcc =  desiredVel[0]
            else:
                factor = 1.0 # détermine la tendance à tourner autour de target (plus petit, plus tourne)            
                self.xAcc = (desiredVel[0] - factor*self.xspeed) / self.mass # force
                self.yAcc = (desiredVel[1] - factor*self.yspeed) / self.mass # force
            accNorm = math.sqrt(self.xAcc**2 + self.yAcc**2)
            if accNorm > A_MAX:
                self.xAcc = A_MAX * self.xAcc / accNorm
                self.yAcc = A_MAX * self.yAcc / accNorm

            """
            speedNorm = math.sqrt(self.xspeed**2 + self.yspeed**2)
            if speedNorm > 0.5: # EN FAIRE UNE CONSTANTE...
                if accNorm > A_MAX:
                    self.xAcc = A_MAX * self.xAcc / accNorm
                    self.yAcc = A_MAX * self.yAcc / accNorm
            else:
                if accNorm > 0.03*A_MAX: # EN FAIRE UNE CONSTANTE...
                    self.xAcc = 0.09*A_MAX * self.xAcc / accNorm # EN FAIRE UNE CONSTANTE...
                    self.yAcc = 0.09*A_MAX * self.yAcc / accNorm # EN FAIRE UNE CONSTANTE...
           """

            self.xspeed += self.xAcc
            self.yspeed += self.yAcc

            speedNorm = math.sqrt(self.xspeed**2 + self.yspeed**2)
            
            if speedNorm > V_MAX:
                self.xspeed *= V_MAX/speedNorm
                self.yspeed *= V_MAX/speedNorm

            speedNorm = math.sqrt(self.xspeed**2 + self.yspeed**2)
            
            self.xHead = self.xspeed/speedNorm
            self.yHead = self.yspeed/speedNorm

            
            self.x += self.xspeed
            self.y += self.yspeed
        # AJOUTER LE CONTENU CI-DESSOUS SI ON VEUT UN RETOURNEMENT DIRECT DU BOT
        #
        #else:
        #    self.xspeed = 0
        #    self.yspeed = 0
        #    self.xAcc = 0
        #    self.yAcc = 0
        
class Obstacle:

    obstacles = []

    def __init__(self, x, y, size):
        self.x = x
        self.y = y
        self.size = size
        self.color = (0, 0, 0)
        Obstacle.obstacles.append(self)

    def draw(self, fenetre):
        pygame.draw.circle(fenetre, self.color, (int(self.x), int(self.y)), int(self.size))


start_game()


















        
