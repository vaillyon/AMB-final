x = 0
str = ""
ar = [0, 0, 0, 0]
list = ["", "", ""]
res = 0
total = 0
count = 0
def add():
    print( "Enter a number"  )
    x= int(input(""))
    res= (20 +3 ) *x +10
    print( res  )

def main():
    add()
    ar[0] = 55
    ar[1] = 100
    ar[2] = -200
    ar[3] = 300
    list[0] = "hi"
    list[1] = "BYTE"
    list[2] = "cheese wizz"
    count= 0
    total= 0
    while count <4 :
        total= total +ar[count]
        count= count +1
    print( total  )
    print( "Enter a number"  )
    x= int(input(""))
    if x >0 :
        print( "x is positive"  )
    else:
        print( "x is non-positive"  )
    count= 0
    while count <3 :
        print( list[count]  )
        count= count +1

main()


