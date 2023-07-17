figure

z= linspace ( -1 ,1 ,200) ; % These are the z points
w =[0.2 ,0.4 ,0.6]; % these are the three values of the normalized diffusion length that we will include in our calculations
c=@(z,w) erf (z/w); % define a function of two variables , z and w
col ={[1 ,0 ,0] ,[0 ,0.5 ,0] ,[0 ,0 ,1]}; % these are the three colors ( rgb format )
linetype ={ '-','--',' -.'}; % these are the three line types we well used (plain , dashed and dash -dot )
axes
hold on

y=erf(z/0.4);
y = rescale(y,0,1);
z = rescale(z,-3,3);

plot (z,y)
hold on

% x = -3:0.1:3;
% y = (1/2)*(1+erf(x/sqrt(2)));
% plot(x,y)
% grid on
% title('CDF of normal distribution with \mu = 0 and \sigma = 1')
% xlabel('x')
% ylabel('CDF')
% 
% 
% x = 0: 10000;
% y = x.*x.*x;
% y=(0.5 - 0.3) * (y-0)/(10000*10000*10000)+0.3;
% plot(x,y)