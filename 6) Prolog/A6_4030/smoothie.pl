store(jamba_smoothies, [bob,ned,joe],
 [ smoothie(berry, [orange, blueberry, strawberry], 2),
 smoothie(tropical, [orange, banana, mango, guava], 3),
 smoothie(blue, [banana, blueberry], 3) ]).

store(mikes_and_mikes, [mike,mike],
 [ smoothie(pinacolada, [orange, pineapple, coconut], 2),
 smoothie(green, [orange, banana, kiwi], 5),
 smoothie(purple, [orange, blueberry, strawberry], 2),
 smoothie(smooth, [orange, banana, mango],1) ]).

store(smoothie_kingdom, [mitchell,zack,jesse],
 [ smoothie(combo1, [strawberry, orange, banana], 2),
 smoothie(combo2, [banana, orange], 5),
 smoothie(combo3, [orange, peach, banana], 2),
 smoothie(combo4, [guava, mango, papaya, orange],1),
 smoothie(combo5, [grapefruit, banana, pear],1) ]).


% Method 1
more_than_four(X) :- store(X,_,[_,_,_,_|_]).

% Method 2
exists(X) :-
     store(_,_,SmoothieList),
     is_in_list_of_smoothies(X,SmoothieList).

% Helper Methods
is_in_list_of_smoothies(X,[smoothie(X,_,_)|_]).
is_in_list_of_smoothies(X,[_|Remaining]) :-
   is_in_list_of_smoothies(X,Remaining).

% Method 3
ratio(SmoothieStore,Ratio) :-
  store(SmoothieStore,Employees,Smoothies),
  length(Employees,NumberOfEmployees),
  length(Smoothies,NumberOfSmoothies),
  Ratio is NumberOfEmployees / NumberOfSmoothies.

% Method 4
average(Store,Average) :-
           store(Store,_,ListOfSmoothies),
           average_price(ListOfSmoothies,Average).
           
% Helper Methods
average_price(SmoothieList,Average) :-
                sum_of_prices(SmoothieList,Sum),
                length(SmoothieList,Number),
                Average is Sum / Number.
sum_of_prices([],0).
sum_of_prices([smoothie(_,_,SmoothiePrice)|Remaining],Price) :-
                     sum_of_prices(Remaining,NewPrice),
                     Price is NewPrice + SmoothiePrice.

