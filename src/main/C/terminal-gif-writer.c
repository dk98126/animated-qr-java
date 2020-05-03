#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>

int main() {
    FILE *in_file = fopen("asciiart.txt", "r");
    int c;

    if (in_file == NULL) {
        printf("Error! Could not open file\n");
    }
    while (1) {
        while ((c = fgetc(in_file)) != EOF) {
            if (c == '\f') {
                usleep(100000);
                system("@cls||clear");
            } else {
                printf("%c", c);
            }
        }
        rewind(in_file);
    }
    return 0;
}
